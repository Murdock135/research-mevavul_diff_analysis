class isAvailable {
@Override
    public boolean isAvailable() {
        return GeoTools.isJNDIAvailable();
    }
}
