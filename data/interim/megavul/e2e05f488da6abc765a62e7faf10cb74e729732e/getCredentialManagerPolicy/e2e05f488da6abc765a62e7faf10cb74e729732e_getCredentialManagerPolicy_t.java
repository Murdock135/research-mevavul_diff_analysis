class getCredentialManagerPolicy {
@Override
    public PackagePolicy getCredentialManagerPolicy(int userId) {
        if (!mHasFeature) {
            return null;
        }
        final CallerIdentity caller = getCallerIdentity();
        Preconditions.checkCallAuthorization(
                canWriteCredentialManagerPolicy(caller) || canQueryAdminPolicy(caller));
        if (userId != caller.getUserId()) {
            Preconditions.checkCallAuthorization(
                    hasCallingOrSelfPermission(permission.INTERACT_ACROSS_USERS));
        }

        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerOrDeviceOwnerLocked(userId);
            return (admin != null) ? admin.mCredentialManagerPolicy : null;
        }
    }
}
