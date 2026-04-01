class setContentType {
private void setContentType(
            final HttpServletResponse statusResponse,
            final String jsonpCallback) {
        if (StringUtils.isEmpty(jsonpCallback)) {
            statusResponse.setContentType("application/json; charset=utf-8");
        } else {
            statusResponse.setContentType("application/javascript; charset=utf-8");
        }
    }
}
