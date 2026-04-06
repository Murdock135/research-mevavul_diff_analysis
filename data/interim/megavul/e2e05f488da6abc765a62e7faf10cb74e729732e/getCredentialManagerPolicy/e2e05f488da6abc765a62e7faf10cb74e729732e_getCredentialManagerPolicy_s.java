class getCredentialManagerPolicy {
@Override
    public PackagePolicy getCredentialManagerPolicy() {
        if (!mHasFeature) {
            return null;
        }
        final CallerIdentity caller = getCallerIdentity();
        Preconditions.checkCallAuthorization(
                canWriteCredentialManagerPolicy(caller) || canQueryAdminPolicy(caller));

        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerOrDeviceOwnerLocked(caller.getUserId());
            return (admin != null) ? admin.mCredentialManagerPolicy : null;
        }
    }
}
