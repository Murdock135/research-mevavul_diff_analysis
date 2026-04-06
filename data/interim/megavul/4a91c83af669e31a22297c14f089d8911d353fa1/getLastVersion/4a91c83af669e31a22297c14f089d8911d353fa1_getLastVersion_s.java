class getLastVersion {
public Version getLastVersion(boolean fetch) {
        boolean checkPreview = previewAble();
        if (updateVersionTimerTask == null) {
            updateVersionTimerTask = new UpdateVersionTimerTask(checkPreview);
        }
        if (fetch) {
            try {
                return updateVersionTimerTask.fetchLastVersion(checkPreview);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return updateVersionTimerTask.getVersion();
    }
}
