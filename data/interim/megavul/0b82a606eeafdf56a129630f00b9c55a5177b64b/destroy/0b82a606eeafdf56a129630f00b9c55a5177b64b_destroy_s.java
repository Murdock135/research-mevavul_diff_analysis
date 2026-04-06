class destroy {
@Override
    public void destroy() {
        ServletContext servletContext = getServletContext();
        Lookup lookup = new VaadinServletContext(servletContext)
                .getAttribute(Lookup.class);
        super.destroy();
        if (lookup == null) {
            return;
        }

        BundleContext bundleContext = FrameworkUtil
                .getBundle(OSGiVaadinServlet.class).getBundleContext();
        Set<Servlet> servlets = new HashSet<>();
        try {
            ServiceReference<?>[] references = bundleContext
                    .getAllServiceReferences(Servlet.class.getName(), null);
            for (ServiceReference<?> reference : references) {
                servlets.addAll(handleDestroy(lookup, reference));
            }
        } catch (InvalidSyntaxException e) {
            // this may not happen because filter parameter is {@code null} so
            // it may not have invalid syntax
            assert false;
        }
        servlets.remove(this);
        if (servlets.size() > 0) {
            return;
        }
        ServiceReference<OSGiVaadinInitialization> reference = bundleContext
                .getServiceReference(OSGiVaadinInitialization.class);
        if (reference == null) {
            return;
        }
        OSGiVaadinInitialization initialization = bundleContext
                .getService(reference);
        initialization
                .contextDestroyed(new ServletContextEvent(servletContext));
    }
}
