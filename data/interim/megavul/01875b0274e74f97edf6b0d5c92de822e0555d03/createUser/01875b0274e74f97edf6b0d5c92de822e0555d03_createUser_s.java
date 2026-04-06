class createUser {
@Override
    public UserInfo createUser(String name, int flags) {
        checkManageUsersPermission("Only the system can create users");
        return createUserInternal(name, flags, UserHandle.USER_NULL);
    }
}
