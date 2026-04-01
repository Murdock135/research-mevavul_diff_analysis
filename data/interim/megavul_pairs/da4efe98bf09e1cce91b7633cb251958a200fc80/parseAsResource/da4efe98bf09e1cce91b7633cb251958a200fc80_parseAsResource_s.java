class parseAsResource {
private RDFDescription parseAsResource(Node node) {
        // See: http://www.w3.org/TR/REC-rdf-syntax/#section-Syntax-parsetype-resource
        List<Entry> entries = new ArrayList<Entry>();

        for (Node child : asIterable(node.getChildNodes())) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            entries.add(new XMPEntry(child.getNamespaceURI() + child.getLocalName(), child.getLocalName(), getChildTextValue(child)));
        }

        return new RDFDescription(entries);
    }
}
