class contextDestroyed {
@Override
    public void contextDestroyed(ServletContextEvent event) {
        initializerClasses.removeContext(event.getServletContext());

        VaadinServletContext servletContext = new VaadinServletContext(
                event.getServletContext());

        AppConfigFactoryTracker tracker = servletContext
                .getAttribute(AppConfigFactoryTracker.class);
        if (tracker != null) {
            tracker.close();
            servletContext.removeAttribute(AppConfigFactoryTracker.class);
        }
    }
}
