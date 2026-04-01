class updateUserPictureAndName {
private boolean updateUserPictureAndName(Profile showUser, String picture, String name) {
		boolean updateProfile = false;
		boolean updateUser = false;
		User u = showUser.getUser();

		if (CONF.avatarEditsEnabled() && !StringUtils.isBlank(picture)) {
			updateProfile = avatarRepository.store(showUser, picture);
		}

		if (CONF.nameEditsEnabled() && !StringUtils.isBlank(name)) {
			showUser.setName(name);
			if (StringUtils.isBlank(showUser.getOriginalName())) {
				showUser.setOriginalName(name);
			}
			if (!u.getName().equals(name)) {
				u.setName(name);
				updateUser = true;
			}
			updateProfile = true;
		}

		if (updateUser) {
			utils.getParaClient().update(u);
		}
		return updateProfile;
	}
}
