class contextDestroyed {
@Override
    public void contextDestroyed(ServletContextEvent event) {
        initializerClasses.removeContext(event.getServletContext());

        VaadinServletContext servletContext = new VaadinServletContext(
                event.getServletContext());

        ServletInitRequirementsTracker tracker = servletContext
                .getAttribute(ServletInitRequirementsTracker.class);
        if (tracker != null) {
            tracker.close();
            servletContext.removeAttribute(ServletInitRequirementsTracker.class);
        }
    }
}
