import xml.etree.ElementTree as ET
import re
from pathlib import Path
from typing import Dict, List, Tuple, Union

from primevul_analysis.types import GumTreeDiff, NodeRef, GumTreeAction, GumTreeMatch

_XML_DECL_RE = re.compile(r"<\?xml[^>]*\?>", re.IGNORECASE)

class GumTreeDiffParser:
    """Parser for GumTree diff XML files."""

    def __init__(self) -> None:
        self.node_re = re.compile(
            r"""^\s*
            (?P<type>[^:\[]+?)          # node type up to ':' or '['
            (?:\s*:\s*(?P<label>.*?))?  # optional ': label'
            \s*\[\s*(?P<start>\d+)\s*,\s*(?P<end>\d+)\s*\]\s*
            $""",
            re.VERBOSE,
        )

    def parse_node_ref(self, node_str: str) -> NodeRef:
        s = (node_str or "").strip()
        m = self.node_re.match(s)
        if not m:
            raise ValueError(f"Cannot parse node ref: {s!r}")

        label = m.group("label")
        label = label.strip() if label is not None else None

        return NodeRef(
            type=m.group("type").strip(),
            label=label,
            start=int(m.group("start")),
            end=int(m.group("end")),
        )

    def _load_root(self, xml_input: Union[str, Path]) -> ET.Element:
        """
        GumTree sometimes emits two top-level elements (<matches>...</matches><actions>...</actions>).
        ElementTree requires a single root, so we wrap in <root> when needed.
        """
        if isinstance(xml_input, Path) or (isinstance(xml_input, str) and Path(xml_input).exists()):
            text = Path(xml_input).read_text(encoding="utf-8", errors="replace")
        else:
            text = str(xml_input)

        text = text.strip() # Remove leading/trailing whitespace
        text = _XML_DECL_RE.sub("", text).strip() # Remove XML declaration

        # Wrap if it looks like multiple top-level elements
        if "<matches" in text and "<actions" in text and not re.search(r"^\s*<root[\s>]", text):
            text = f"<root>{text}</root>"

        return ET.fromstring(text)

    def parse(self, gumtree_diff_xml: Union[str, Path]) -> GumTreeDiff:
        root = self._load_root(gumtree_diff_xml)

        matches: List[GumTreeMatch] = []
        actions: List[GumTreeAction] = []

        # Parse matches (attributes are src + dest)
        for match in root.findall(".//matches/match"):
            src_txt = match.get("src")
            dest_txt = match.get("dest")  # IMPORTANT: GumTree uses dest, not dst

            if not src_txt or not dest_txt:
                continue

            src_node = self.parse_node_ref(src_txt)
            dst_node = self.parse_node_ref(dest_txt)
            matches.append(GumTreeMatch(src=src_node, dst=dst_node))

        # Parse actions (tag is <actions>, children are action elements)
        for action_el in root.findall(".//actions/*"):
            kind = action_el.tag
            tree_txt = action_el.get("tree")
            parent_txt = action_el.get("parent")
            at_txt = action_el.get("at")
            label = action_el.get("label")

            tree_node = self.parse_node_ref(tree_txt) if tree_txt else None
            parent_node = self.parse_node_ref(parent_txt) if parent_txt else None
            at = int(at_txt) if at_txt is not None else None

            actions.append(
                GumTreeAction(
                    kind=kind,
                    tree=tree_node,
                    parent=parent_node,
                    at=at,
                    label=label,
                    raw_attributes=dict(action_el.attrib),
                )
            )

        # Build src_to_dst and dst_to_src maps (span-based)
        src_to_dst: Dict[Tuple[int, int], NodeRef] = {}
        dst_to_src: Dict[Tuple[int, int], NodeRef] = {}

        for m in matches:
            src_to_dst[m.src.span] = m.dst
            dst_to_src[m.dst.span] = m.src

        return GumTreeDiff(
            actions=actions,
            matches=matches,
            src_to_dst=src_to_dst,
            dst_to_src=dst_to_src,
        )
    
    def parse_batch(self, gumtree_diff_xmls: List[Union[str, Path]]) -> List[GumTreeDiff]:
        """Parse multiple GumTree diff XML inputs"""
        return [self.parse(xml_input) for xml_input in gumtree_diff_xmls]
        
# Example usage:
# parser = GumTreeDiffParser()
# gumtree_diff = parser.parse("path_to_gumtree_diff.xml")