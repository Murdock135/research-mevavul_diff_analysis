class templatePath {
private String templatePath() {
        if (templatePath == null) {
            String file = HgCommand.class.getResource("/hg.template").getFile();
            try {
                templatePath = URLDecoder.decode(new File(file).getAbsolutePath(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                templatePath = URLDecoder.decode(new File(file).getAbsolutePath());
            }
        }
        return templatePath;
    }
}
