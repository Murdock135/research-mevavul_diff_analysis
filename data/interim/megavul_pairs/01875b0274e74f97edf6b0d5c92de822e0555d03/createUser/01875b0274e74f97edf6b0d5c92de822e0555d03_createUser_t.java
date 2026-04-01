class createUser {
@Override
    public UserInfo createUser(String name, int flags) {
        checkManageOrCreateUsersPermission(flags);
        return createUserInternal(name, flags, UserHandle.USER_NULL);
    }
}
