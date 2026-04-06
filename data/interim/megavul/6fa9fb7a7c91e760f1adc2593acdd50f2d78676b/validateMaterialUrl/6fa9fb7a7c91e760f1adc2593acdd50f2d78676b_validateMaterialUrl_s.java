class validateMaterialUrl {
protected void validateMaterialUrl(UrlArgument url) {
        if (url == null || isBlank(url.forDisplay())) {
            errors().add(URL, "URL cannot be blank");
            return;
        }
    }
}
