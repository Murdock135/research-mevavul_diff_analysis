class consume {
public boolean consume(HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse) throws IOException {
        try {
            if (consumeWithFileResourceHandlers(httpRequest, httpResponse)) {
                return true;
            }

            if (consumeWithJarResourceHandler(httpRequest, httpResponse)) {
                return true;
            }
        } catch (DirectoryTraversal.DirectoryTraversalDetection directoryTraversalDetection) {
            LOG.warn("directoryTraversalDetection for path: " + httpRequest.getPathInfo());
        }
        return false;
    }
}
