class systemReady {
void systemReady() {
        synchronized (mInstallLock) {
            synchronized (mPackagesLock) {
                // Prune out any partially created/partially removed users.
                ArrayList<UserInfo> partials = new ArrayList<UserInfo>();
                for (int i = 0; i < mUsers.size(); i++) {
                    UserInfo ui = mUsers.valueAt(i);
                    if ((ui.partial || ui.guestToRemove) && i != 0) {
                        partials.add(ui);
                    }
                }
                for (int i = 0; i < partials.size(); i++) {
                    UserInfo ui = partials.get(i);
                    Slog.w(LOG_TAG, "Removing partially created user " + ui.id
                            + " (name=" + ui.name + ")");
                    removeUserStateLocked(ui.id);
                }
            }
        }
        onUserForeground(UserHandle.USER_OWNER);
        mAppOpsService = IAppOpsService.Stub.asInterface(
                ServiceManager.getService(Context.APP_OPS_SERVICE));
        for (int i = 0; i < mUserIds.length; ++i) {
            try {
                mAppOpsService.setUserRestrictions(mUserRestrictions.get(mUserIds[i]), mUserIds[i]);
            } catch (RemoteException e) {
                Log.w(LOG_TAG, "Unable to notify AppOpsService of UserRestrictions");
            }
        }
    }
}
