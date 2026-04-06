class isAvailable_1 {
@Override
    public boolean isAvailable() {
        try {
            GeoTools.getInitialContext();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
