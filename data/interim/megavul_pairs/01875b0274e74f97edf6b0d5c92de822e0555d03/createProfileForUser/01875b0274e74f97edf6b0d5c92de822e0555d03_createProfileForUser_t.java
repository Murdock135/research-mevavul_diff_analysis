class createProfileForUser {
@Override
    public UserInfo createProfileForUser(String name, int flags, int userId) {
        checkManageOrCreateUsersPermission(flags);
        if (userId != UserHandle.USER_OWNER) {
            Slog.w(LOG_TAG, "Only user owner can have profiles");
            return null;
        }
        return createUserInternal(name, flags, userId);
    }
}
