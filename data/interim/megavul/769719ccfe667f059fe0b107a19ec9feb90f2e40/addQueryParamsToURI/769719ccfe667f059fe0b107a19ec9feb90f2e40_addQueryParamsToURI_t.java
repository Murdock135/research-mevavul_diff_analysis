class addQueryParamsToURI {
public URI addQueryParamsToURI(URI uri, List<Property> allQueryParams, boolean encodeParamsToggle) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        uriBuilder.uri(uri);

        if (allQueryParams != null) {
            for (Property queryParam : allQueryParams) {
                String key = queryParam.getKey();
                if (isNotEmpty(key)) {
                    if (encodeParamsToggle) {
                        uriBuilder.queryParam(
                                URLEncoder.encode(key, StandardCharsets.UTF_8),
                                URLEncoder.encode((String) queryParam.getValue(), StandardCharsets.UTF_8)
                        );
                    } else {
                        uriBuilder.queryParam(
                                key,
                                queryParam.getValue()
                        );
                    }
                }
            }
        }

        return uriBuilder.build(true).toUri();
    }
}
