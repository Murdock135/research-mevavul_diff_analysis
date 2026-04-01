class validateMaterialUrl {
protected void validateMaterialUrl(UrlArgument url) {
        if (url == null || isBlank(url.forDisplay())) {
            errors().add(URL, "URL cannot be blank");
            return;
        }

        if (System.getProperty("gocd.verify.url.correctness", "y").equalsIgnoreCase("y") && !url.isValidURLOrLocalPath()) {
            errors().add(URL, "URL does not seem to be valid.");
        }
    }
}
