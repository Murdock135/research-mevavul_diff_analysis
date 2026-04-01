class setImageToShow {
public void setImageToShow(String imageToShow) {
        synchronized (lock) {
            this.imageToShow = imageToShow;
            if (viewManager != null) {
                viewManager.setDropdownSelected(String.valueOf(imageToShow));
            }
            // Reset LOGID (the LOGID setter is called later by PrettyFaces, so if a value is passed, it will still be set)
            setLogid("");
            logger.trace("imageToShow: {}", this.imageToShow);
        }
    }
}
