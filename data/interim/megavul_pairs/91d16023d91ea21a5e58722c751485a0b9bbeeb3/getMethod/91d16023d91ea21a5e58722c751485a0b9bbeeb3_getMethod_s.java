class getMethod {
public Method getMethod(HttpServletRequest request) {
        String path = getPath(request);
        if (path == null) {
            return null;
        }
        String httpMethod = request.getMethod();
        String urlKey = httpMethod + REQUEST_PATH_SEPARATOR + path.replaceFirst(EnvUtil.getContextPath(), "");
        List<RequestMappingInfo> requestMappingInfos = urlLookup.get(urlKey);
        if (CollectionUtils.isEmpty(requestMappingInfos)) {
            return null;
        }
        List<RequestMappingInfo> matchedInfo = findMatchedInfo(requestMappingInfos, request);
        if (CollectionUtils.isEmpty(matchedInfo)) {
            return null;
        }
        RequestMappingInfo bestMatch = matchedInfo.get(0);
        if (matchedInfo.size() > 1) {
            RequestMappingInfoComparator comparator = new RequestMappingInfoComparator();
            matchedInfo.sort(comparator);
            bestMatch = matchedInfo.get(0);
            RequestMappingInfo secondBestMatch = matchedInfo.get(1);
            if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                throw new IllegalStateException(
                        "Ambiguous methods mapped for '" + request.getRequestURI() + "': {" + bestMatch + ", "
                                + secondBestMatch + "}");
            }
        }
        return methods.get(bestMatch);
    }
}
