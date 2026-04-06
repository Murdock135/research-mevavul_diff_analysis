class isAvailable {
@Override
    public boolean isAvailable() {
        try {
            GeoTools.getInitialContext();
            return true;
        } catch (NamingException e) {
            return false;
        }
    }
}
