class createTmpDir {
private String createTmpDir() {
		try {
			File tmp = File.createTempFile("fileresourcemanager", null);
			tmp.delete();
			tmp.mkdir();
			String workDir = tmp.getAbsolutePath();
			return workDir;
		} catch (IOException e) {
			throw log.errorCreateWorkDir(e);
		}
	}
}
