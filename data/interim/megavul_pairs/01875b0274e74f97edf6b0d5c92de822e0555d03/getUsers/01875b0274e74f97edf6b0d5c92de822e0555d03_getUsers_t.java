class getUsers {
@Override
    public List<UserInfo> getUsers(boolean excludeDying) {
        checkManageOrCreateUsersPermission("query users");
        synchronized (mPackagesLock) {
            ArrayList<UserInfo> users = new ArrayList<UserInfo>(mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                UserInfo ui = mUsers.valueAt(i);
                if (ui.partial) {
                    continue;
                }
                if (!excludeDying || !mRemovingUserIds.get(ui.id)) {
                    users.add(ui);
                }
            }
            return users;
        }
    }
}
