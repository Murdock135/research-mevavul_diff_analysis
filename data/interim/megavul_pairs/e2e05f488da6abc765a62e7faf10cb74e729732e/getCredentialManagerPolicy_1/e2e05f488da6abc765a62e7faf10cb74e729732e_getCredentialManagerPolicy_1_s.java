class getCredentialManagerPolicy_1 {
public @Nullable PackagePolicy getCredentialManagerPolicy() {
        throwIfParentInstance("getCredentialManagerPolicy");
        if (mService != null) {
            try {
                return mService.getCredentialManagerPolicy();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }
}
