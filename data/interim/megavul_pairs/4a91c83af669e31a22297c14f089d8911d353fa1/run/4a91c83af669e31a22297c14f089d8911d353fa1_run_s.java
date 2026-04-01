class run {
@Override
    public void run() {
        try {
            this.version = fetchLastVersion(checkPreview);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
