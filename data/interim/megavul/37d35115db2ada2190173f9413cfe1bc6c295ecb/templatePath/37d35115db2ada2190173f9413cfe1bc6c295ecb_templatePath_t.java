class templatePath {
private String templatePath() {
        if (templatePath == null) {
            String file = HgCommand.class.getResource("/hg.template").getFile();
            templatePath = URLDecoder.decode(new File(file).getAbsolutePath(), StandardCharsets.UTF_8);
        }
        return templatePath;
    }
}
