class getUserInfo {
@Override
    public UserInfo getUserInfo(int userId) {
        checkManageUsersPermission("query user");
        synchronized (mPackagesLock) {
            return getUserInfoLocked(userId);
        }
    }
}
