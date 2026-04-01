class getUploaded_1 {
@Override
	public File getUploaded(Long projectId) {
		return new File(Bootstrap.getSiteDir(), "avatars/uploaded/projects/" + projectId + ".jpg");
	}
}
