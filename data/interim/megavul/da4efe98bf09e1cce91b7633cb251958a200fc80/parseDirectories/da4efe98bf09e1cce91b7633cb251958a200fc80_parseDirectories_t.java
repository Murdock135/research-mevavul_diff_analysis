class parseDirectories {
private XMPDirectory parseDirectories(final Node pParentNode, NodeList pNodes, String toolkit) {
        Map<String, List<Entry>> subdirs = new LinkedHashMap<>();

        for (Node desc : asIterable(pNodes)) {
            if (desc.getParentNode() != pParentNode) {
                continue;
            }

            // Support attribute short-hand syntax
            parseAttributesForKnownElements(subdirs, desc);

            for (Node node : asIterable(desc.getChildNodes())) {
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                // Lookup
                List<Entry> dir = subdirs.get(node.getNamespaceURI());
                if (dir == null) {
                    dir = new ArrayList<>();
                    subdirs.put(node.getNamespaceURI(), dir);
                }

                Object value;

                if (isResourceType(node)) {
                    value = parseAsResource(node);
                }
                else {
                    // TODO: This method contains loads of duplication an should be cleaned up...
                    // Support attribute short-hand syntax
                    Map<String, List<Entry>> subsubdirs = new LinkedHashMap<>();

                    parseAttributesForKnownElements(subsubdirs, node);

                    if (!subsubdirs.isEmpty()) {
                        List<Entry> entries = new ArrayList<>(subsubdirs.size());

                        for (Map.Entry<String, List<Entry>> entry : subsubdirs.entrySet()) {
                            entries.addAll(entry.getValue());
                        }

                        value = new RDFDescription(entries);
                    }
                    else {
                        value = getChildTextValue(node);
                    }
                }

                dir.add(new XMPEntry(node.getNamespaceURI() + node.getLocalName(), node.getLocalName(), value));
            }
        }

        List<Directory> entries = new ArrayList<>(subdirs.size());

        // TODO: Should we still allow asking for a subdirectory by item id?
        for (Map.Entry<String, List<Entry>> entry : subdirs.entrySet()) {
            entries.add(new RDFDescription(entry.getKey(), entry.getValue()));
        }

        return new XMPDirectory(entries, toolkit);
    }
}
