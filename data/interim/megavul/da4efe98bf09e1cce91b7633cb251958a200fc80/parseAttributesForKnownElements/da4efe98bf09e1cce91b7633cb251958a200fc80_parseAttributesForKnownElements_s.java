class parseAttributesForKnownElements {
private void parseAttributesForKnownElements(Map<String, List<Entry>> subdirs, Node desc) {
        // NOTE: NamedNodeMap does not have any particular order...
        NamedNodeMap attributes = desc.getAttributes();

        for (Node attr : asIterable(attributes)) {
            if (!XMP.ELEMENTS.contains(attr.getNamespaceURI())) {
                continue;
            }

            List<Entry> dir = subdirs.get(attr.getNamespaceURI());

            if (dir == null) {
                dir = new ArrayList<Entry>();
                subdirs.put(attr.getNamespaceURI(), dir);
            }

            dir.add(new XMPEntry(attr.getNamespaceURI() + attr.getLocalName(), attr.getLocalName(), attr.getNodeValue()));
        }
    }
}
