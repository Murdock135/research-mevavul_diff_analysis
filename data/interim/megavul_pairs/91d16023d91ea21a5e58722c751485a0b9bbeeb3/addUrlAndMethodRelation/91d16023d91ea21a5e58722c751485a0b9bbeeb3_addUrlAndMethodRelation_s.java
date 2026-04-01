class addUrlAndMethodRelation {
private void addUrlAndMethodRelation(String urlKey, String[] requestParam, Method method) {
        RequestMappingInfo requestMappingInfo = new RequestMappingInfo();
        requestMappingInfo.setPathRequestCondition(new PathRequestCondition(urlKey));
        requestMappingInfo.setParamRequestCondition(new ParamRequestCondition(requestParam));
        List<RequestMappingInfo> requestMappingInfos = urlLookup.get(urlKey);
        if (requestMappingInfos == null) {
            urlLookup.putIfAbsent(urlKey, new ArrayList<>());
            requestMappingInfos = urlLookup.get(urlKey);
        }
        requestMappingInfos.add(requestMappingInfo);
        methods.put(requestMappingInfo, method);
    }
}
