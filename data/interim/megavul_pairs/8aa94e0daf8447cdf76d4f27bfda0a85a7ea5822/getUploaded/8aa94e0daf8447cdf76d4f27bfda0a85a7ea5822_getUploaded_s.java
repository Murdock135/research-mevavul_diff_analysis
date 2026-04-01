class getUploaded {
@Override
	public File getUploaded(User user) {
		return new File(Bootstrap.getSiteDir(), "avatars/uploaded/users/" + user.getId() + ".jpg");
	}
}
