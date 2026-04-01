class configureJarCase {
private boolean configureJarCase(String folder, ClassPathResource resource) throws IOException {
        if (resource.getURL().getProtocol().equals("jar")) {

            InputStream stream = StaticFilesConfiguration.class.getResourceAsStream(folder);

            if (stream != null) {
                if (jarResourceHandlers == null) {
                    jarResourceHandlers = new ArrayList<>();
                }

                // Add jar file resource handler
                jarResourceHandlers.add(new JarResourceHandler(folder, "index.html"));
                staticResourcesSet = true;
                return true;
            }

            LOG.error("Static file configuration failed.");
        }
        return false;
    }
}
