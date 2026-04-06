class getEndPoint {
private Endpoint getEndPoint(String hint) throws URISyntaxException
    {
        // TODO: use URI directly when upgrading to a version of XWiki providing a URI converter
        String uriString = getProperty(PROPPREFIX_ENDPOINT + hint, String.class);

        // If no direct endpoint is provider assume it's a XWiki OIDC provider and generate the endpoint from the hint
        URI uri;
        if (uriString == null) {
            if (getProperty(PROP_XWIKIPROVIDER, String.class) != null) {
                uri = this.manager.createEndPointURI(getXWikiProvider().toString(), hint);
            } else {
                uri = null;
            }
        } else {
            uri = new URI(uriString);
        }

        // If we still don't have any endpoint URI, return null
        if (uri == null) {
            return null;
        }

        // Find custom headers
        Map<String, List<String>> headers = new LinkedHashMap<>();

        List<String> entries = getProperty(PROPPREFIX_ENDPOINT + hint + ".headers", List.class);
        if (entries != null) {
            for (String entry : entries) {
                int index = entry.indexOf(':');

                if (index > 0 && index < entry.length() - 1) {
                    headers.computeIfAbsent(entry.substring(0, index), key -> new ArrayList<>())
                        .add(entry.substring(index + 1));
                }
            }
        }

        return new Endpoint(uri, headers);
    }
}
