class setAccountManagementDisabled {
@Override
    public void setAccountManagementDisabled(ComponentName who, String accountType,
            boolean disabled, boolean parent) {
        if (!mHasFeature) {
            return;
        }
        Objects.requireNonNull(who, "ComponentName is null");
        final CallerIdentity caller = getCallerIdentity(who);
        synchronized (getLockObject()) {
            /*
             * When called on the parent DPM instance (parent == true), affects active admin
             * selection in two ways:
             * * The ActiveAdmin must be of an org-owned profile owner.
             * * The parent ActiveAdmin instance should be used for managing the restriction.
             */
            final ActiveAdmin ap;
            if (parent) {
                ap = getParentOfAdminIfRequired(getOrganizationOwnedProfileOwnerLocked(caller),
                        parent);
            } else {
                ap = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(caller), parent);
            }

            if (disabled) {
                ap.accountTypesWithManagementDisabled.add(accountType);
            } else {
                ap.accountTypesWithManagementDisabled.remove(accountType);
            }
            saveSettingsLocked(UserHandle.getCallingUserId());
        }
    }
}
