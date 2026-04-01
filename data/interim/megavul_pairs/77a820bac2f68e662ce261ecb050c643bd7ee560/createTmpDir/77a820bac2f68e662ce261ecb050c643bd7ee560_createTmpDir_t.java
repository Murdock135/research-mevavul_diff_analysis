class createTmpDir {
private String createTmpDir() {
        try {
            File tmp = Files.createTempDirectory("fileresourcemanager").toFile();
            String workDir = tmp.getAbsolutePath();
            return workDir;
        } catch (IOException e) {
            throw log.errorCreateWorkDir(e);
        }
    }
}
