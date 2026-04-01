class configure {
public synchronized void configure(String folder) {
        Assert.notNull(folder, "'folder' must not be null");

        if (!staticResourcesSet) {
            try {
                ClassPathResource resource = new ClassPathResource(folder);

                if (configureJarCase(folder, resource)) {
                    return;
                }

                if (!resource.getFile().isDirectory()) {
                    LOG.error("Static resource location must be a folder");
                    return;
                }

                if (staticResourceHandlers == null) {
                    staticResourceHandlers = new ArrayList<>();
                }

                staticResourceHandlers.add(new ClassPathResourceHandler(folder, "index.html"));
                LOG.info("StaticResourceHandler configured with folder = " + folder);
            } catch (IOException e) {
                LOG.error("Error when creating StaticResourceHandler", e);
            }
            staticResourcesSet = true;
        }

    }
}
