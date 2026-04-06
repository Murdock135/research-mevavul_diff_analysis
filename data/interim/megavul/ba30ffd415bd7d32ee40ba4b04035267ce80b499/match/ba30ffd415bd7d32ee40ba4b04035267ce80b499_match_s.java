class match {
@SuppressWarnings("unchecked")
    private boolean match(final XPathFilter xPathFilter, final SAXEventRecorder steppingFilter) throws Exception {
        final Configuration configuration = steppingFilter.getConfiguration();
        final NodeInfo nodeInfo = steppingFilter.getEvents();
        final NamespaceContext namespaceContext = steppingFilter.getNamespaceContext();

        final SAXEventRecorder.CompiledXPathFilter compiledXPathFilter = new SAXEventRecorder.CompiledXPathFilter(
                xPathFilter, configuration, namespaceContext);
        final Object result = compiledXPathFilter.getXPathExpression().evaluate(nodeInfo, XPathConstants.NODESET);

        final List<NodeInfo> nodes = (List<NodeInfo>) result;
        if (nodes.size() > 0) {
            switch (xPathFilter.getMatchType()) {
                case EXISTS:
                    return true;

                case CONTAINS:
                    for (int i = 0; i < nodes.size(); i++) {
                        final NodeInfo node = nodes.get(i);
                        if (contains(node.getStringValue(), xPathFilter.getValue(), xPathFilter.isIgnoreCase())) {
                            return true;
                        }
                    }
                    break;

                case EQUALS:
                    for (int i = 0; i < nodes.size(); i++) {
                        final NodeInfo node = nodes.get(i);
                        if (equals(node.getStringValue(), xPathFilter.getValue(), xPathFilter.isIgnoreCase())) {
                            return true;
                        }
                    }
                    break;
                case UNIQUE:
                    return true;
            }
        }

        return false;
    }
}
