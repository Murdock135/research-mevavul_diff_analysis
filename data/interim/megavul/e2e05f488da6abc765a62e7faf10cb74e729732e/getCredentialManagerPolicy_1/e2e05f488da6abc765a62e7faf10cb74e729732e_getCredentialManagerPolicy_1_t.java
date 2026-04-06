class getCredentialManagerPolicy_1 {
@UserHandleAware(
            enabledSinceTargetSdkVersion = UPSIDE_DOWN_CAKE,
            requiresPermissionIfNotCaller = INTERACT_ACROSS_USERS)
    public @Nullable PackagePolicy getCredentialManagerPolicy() {
        throwIfParentInstance("getCredentialManagerPolicy");
        if (mService != null) {
            try {
                return mService.getCredentialManagerPolicy(myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }
}
