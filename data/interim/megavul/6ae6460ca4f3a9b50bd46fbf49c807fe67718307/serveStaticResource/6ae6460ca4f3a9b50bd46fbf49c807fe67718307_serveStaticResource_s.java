class serveStaticResource {
@Override
    public boolean serveStaticResource(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String filenameWithPath = getRequestFilename(request);
        if (HandlerHelper.isPathUnsafe(filenameWithPath)) {
            getLogger().info(HandlerHelper.UNSAFE_PATH_ERROR_MESSAGE_PATTERN,
                    filenameWithPath);
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }

        URL resourceUrl = null;
        if (isAllowedVAADINBuildUrl(filenameWithPath)) {
            resourceUrl = servletService.getClassLoader()
                    .getResource("META-INF" + filenameWithPath);
        }
        if (resourceUrl == null) {
            resourceUrl = servletService.getStaticResource(filenameWithPath);
        }
        if (resourceUrl == null && shouldFixIncorrectWebjarPaths()
                && isIncorrectWebjarPath(filenameWithPath)) {
            // Flow issue #4601
            resourceUrl = servletService.getStaticResource(
                    fixIncorrectWebjarPath(filenameWithPath));
        }

        if (resourceUrl == null) {
            // Not found in webcontent or in META-INF/resources in some JAR
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }

        // There is a resource!

        // Intentionally writing cache headers also for 304 responses
        writeCacheHeaders(filenameWithPath, response);

        long timestamp = writeModificationTimestamp(resourceUrl, request,
                response);
        if (browserHasNewestVersion(request, timestamp)) {
            // Browser is up to date, nothing further to do than set the
            // response code
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        responseWriter.writeResponseContents(filenameWithPath, resourceUrl,
                request, response);
        return true;
    }
}
