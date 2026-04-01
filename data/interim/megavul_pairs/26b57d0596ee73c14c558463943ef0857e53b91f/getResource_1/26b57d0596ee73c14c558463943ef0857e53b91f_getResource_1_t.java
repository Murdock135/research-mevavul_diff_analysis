class getResource_1 {
@Override
    protected AbstractFileResolvingResource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        try {
            path = UriPath.canonical(path);

            final String addedPath = addPaths(baseResource, path);

            ExternalResource resource = new ExternalResource(addedPath);

            DirectoryTraversal.protectAgainstForExternal(resource.getPath());

            if (resource.exists() && resource.isDirectory()) {
                if (welcomeFile != null) {
                    resource = new ExternalResource(addPaths(resource.getPath(), welcomeFile));
                } else {
                    //  No welcome file configured, serve nothing since it's a directory
                    resource = null;
                }
            }

            return (resource != null && resource.exists()) ? resource : null;
        } catch (DirectoryTraversal.DirectoryTraversalDetection directoryTraversalDetection) {
            throw directoryTraversalDetection;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getClass().getSimpleName() + " when trying to get resource. " + e.getMessage());
            }
        }
        return null;
    }
}
