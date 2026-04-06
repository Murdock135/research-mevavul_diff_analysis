class setAffiliationIds {
@Override
    public void setAffiliationIds(ComponentName admin, List<String> ids) {
        if (!mHasFeature) {
            return;
        }
        if (ids == null) {
            throw new IllegalArgumentException("ids must not be null");
        }
        for (String id : ids) {
            Preconditions.checkArgument(!TextUtils.isEmpty(id), "ids must not have empty string");
            enforceMaxStringLength(id, "affiliation id");
        }

        final Set<String> affiliationIds = new ArraySet<>(ids);
        final CallerIdentity caller = getCallerIdentity(admin);
        Preconditions.checkCallAuthorization(
                isProfileOwner(caller) || isDefaultDeviceOwner(caller));
        final int callingUserId = caller.getUserId();

        synchronized (getLockObject()) {
            getUserData(callingUserId).mAffiliationIds = affiliationIds;
            saveSettingsLocked(callingUserId);
            if (callingUserId != UserHandle.USER_SYSTEM && isDeviceOwner(admin, callingUserId)) {
                // Affiliation ids specified by the device owner are additionally stored in
                // UserHandle.USER_SYSTEM's DevicePolicyData.
                getUserData(UserHandle.USER_SYSTEM).mAffiliationIds = affiliationIds;
                saveSettingsLocked(UserHandle.USER_SYSTEM);
            }

            // Affiliation status for any user, not just the calling user, might have changed.
            // The device owner user will still be affiliated after changing its affiliation ids,
            // but as a result of that other users might become affiliated or un-affiliated.
            maybePauseDeviceWideLoggingLocked();
            maybeResumeDeviceWideLoggingLocked();
            maybeClearLockTaskPolicyLocked();
            updateAdminCanGrantSensorsPermissionCache(callingUserId);
        }
    }
}
