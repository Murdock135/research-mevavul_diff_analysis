class createHandler {
@Override
    public StaticFileHandler createHandler(VaadinServletService service) {
        return new OSGiStaticFileHandler(service);
    }
}
