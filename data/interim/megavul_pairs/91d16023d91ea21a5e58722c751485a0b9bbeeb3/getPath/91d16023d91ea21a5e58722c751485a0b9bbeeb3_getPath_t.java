class getPath {
private String getPath(HttpServletRequest request) {
        try {
            return new URI(request.getRequestURI()).getPath();
        } catch (URISyntaxException e) {
            LOGGER.error("parse request to path error", e);
            throw new NacosRuntimeException(NacosException.NOT_FOUND, "Invalid URI");
        }
    }
}
