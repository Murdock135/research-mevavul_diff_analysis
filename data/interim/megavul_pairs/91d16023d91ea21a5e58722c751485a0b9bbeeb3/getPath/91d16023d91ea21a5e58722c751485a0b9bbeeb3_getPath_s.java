class getPath {
private String getPath(HttpServletRequest request) {
        String path = null;
        try {
            path = new URI(request.getRequestURI()).getPath();
        } catch (URISyntaxException e) {
            LOGGER.error("parse request to path error", e);
        }
        return path;
    }
}
