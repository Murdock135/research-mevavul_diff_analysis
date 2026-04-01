class createHandler {
@Override
    public StaticFileHandler createHandler(VaadinService service) {
        if (service instanceof VaadinServletService) {
            return new OSGiStaticFileHandler((VaadinServletService) service);
        }
        return null;
    }
}
