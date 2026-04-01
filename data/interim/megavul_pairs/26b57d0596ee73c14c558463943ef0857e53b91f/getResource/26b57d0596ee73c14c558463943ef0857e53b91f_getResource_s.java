class getResource {
@Override
    protected AbstractFileResolvingResource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        try {
            path = UriPath.canonical(path);

            final String addedPath = addPaths(baseResource, path);

            ClassPathResource resource = new ClassPathResource(addedPath);

            if (resource.exists() && resource.getFile().isDirectory()) {
                if (welcomeFile != null) {
                    resource = new ClassPathResource(addPaths(resource.getPath(), welcomeFile));
                } else {
                    //  No welcome file configured, serve nothing since it's a directory
                    resource = null;
                }
            }

            return (resource != null && resource.exists()) ? resource : null;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getClass().getSimpleName() + " when trying to get resource. " + e.getMessage());
            }
        }
        return null;
    }
}
