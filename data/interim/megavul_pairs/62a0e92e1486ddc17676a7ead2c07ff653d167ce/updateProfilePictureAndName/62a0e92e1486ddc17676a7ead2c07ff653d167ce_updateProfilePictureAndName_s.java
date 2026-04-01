class updateProfilePictureAndName {
private boolean updateProfilePictureAndName(Profile authUser, User u) {
		boolean update = false;
		if (!StringUtils.equals(u.getPicture(), authUser.getPicture())
				&& !gravatarAvatarGenerator.isLink(authUser.getPicture())
				&& !CONF.avatarEditsEnabled()) {
			authUser.setPicture(u.getPicture());
			update = true;
		}
		if (!CONF.nameEditsEnabled() &&	!StringUtils.equals(u.getName(), authUser.getName())) {
			authUser.setName(u.getName());
			update = true;
		}
		if (!StringUtils.equals(u.getName(), authUser.getOriginalName())) {
			authUser.setOriginalName(u.getName());
			update = true;
		}
		return update;
	}
}
