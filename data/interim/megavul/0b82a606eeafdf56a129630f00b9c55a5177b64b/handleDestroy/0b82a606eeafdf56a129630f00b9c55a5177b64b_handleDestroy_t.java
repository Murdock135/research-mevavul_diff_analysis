class handleDestroy {
private Set<Servlet> handleDestroy(Lookup lookup,
            ServiceReference<?> reference) {
        Set<Servlet> servlets = new HashSet<>();
        Bundle[] usingBundles = reference.getUsingBundles();
        usingBundles = usingBundles == null ? new Bundle[0] : usingBundles;
        for (Bundle bundle : usingBundles) {
            Servlet servlet = (Servlet) bundle.getBundleContext()
                    .getService(reference);
            if (servlet instanceof OSGiVaadinServlet) {
                ServletContext servletContext = ((VaadinServlet) servlet)
                        .getServletContext();
                Lookup servletLookup = new VaadinServletContext(servletContext)
                        .getAttribute(Lookup.class);
                if (servletLookup == lookup) {
                    servlets.add(servlet);
                }
            }
        }
        return servlets;
    }
}
