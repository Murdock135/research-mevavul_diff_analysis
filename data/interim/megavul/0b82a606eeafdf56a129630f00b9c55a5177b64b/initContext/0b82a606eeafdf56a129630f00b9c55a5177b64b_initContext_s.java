class initContext {
private void initContext(VaadinContext context) {
        context.removeAttribute(VaadinContextInitializer.class);

        VaadinServletContext servletContext = (VaadinServletContext) context;
        try {
            AppConfigFactoryTracker tracker = new AppConfigFactoryTracker(
                    findBundle(servletContext), servletContext,
                    initializerClasses);
            servletContext.setAttribute(tracker);
            tracker.open();
        } catch (IllegalContextState exception) {
            LoggerFactory.getLogger(OSGiVaadinInitialization.class)
                    .warn("Couldn't initialize Vaadin Context", exception);
        }
    }
}
