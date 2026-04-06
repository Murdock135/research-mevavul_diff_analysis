class getNextElementUpwards {
private DomNode getNextElementUpwards(final DomNode startingNode) {
            if (startingNode == DomNode.this) {
                return null;
            }

            DomNode parent = startingNode.getParentNode();
            while (parent != null && parent != DomNode.this) {
                DomNode next = parent.getNextSibling();
                while (next != null && !isAccepted(next)) {
                    next = next.getNextSibling();
                }
                if (next != null) {
                    return next;
                }
                parent = parent.getParentNode();
            }
            return null;
        }
}
