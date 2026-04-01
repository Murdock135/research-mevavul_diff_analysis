class getUserInfo {
@Override
    public UserInfo getUserInfo(int userId) {
        checkManageOrCreateUsersPermission("query user");
        synchronized (mPackagesLock) {
            return getUserInfoLocked(userId);
        }
    }
}
