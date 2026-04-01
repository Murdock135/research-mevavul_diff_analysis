class mutateSecureSetting {
private boolean mutateSecureSetting(String name, String value, int requestingUserId,
            int operation, boolean forceNotify) {
        // Make sure the caller can change the settings.
        enforceWritePermission(Manifest.permission.WRITE_SECURE_SETTINGS);

        // Resolve the userId on whose behalf the call is made.
        final int callingUserId = resolveCallingUserIdEnforcingPermissionsLocked(requestingUserId);

        // If this is a setting that is currently restricted for this user, do not allow
        // unrestricting changes.
        if (isGlobalOrSecureSettingRestrictedForUser(name, callingUserId, value,
                Binder.getCallingUid())) {
            return false;
        }

        // Determine the owning user as some profile settings are cloned from the parent.
        final int owningUserId = resolveOwningUserIdForSecureSettingLocked(callingUserId, name);

        // Only the owning user can change the setting.
        if (owningUserId != callingUserId) {
            return false;
        }

        // Special cases for location providers (sigh).
        if (Settings.Secure.LOCATION_PROVIDERS_ALLOWED.equals(name)) {
            return updateLocationProvidersAllowedLocked(value, owningUserId, forceNotify);
        }

        // Mutate the value.
        synchronized (mLock) {
            switch (operation) {
                case MUTATION_OPERATION_INSERT: {
                    return mSettingsRegistry.insertSettingLocked(SETTINGS_TYPE_SECURE,
                            owningUserId, name, value, getCallingPackage(), forceNotify);
                }

                case MUTATION_OPERATION_DELETE: {
                    return mSettingsRegistry.deleteSettingLocked(SETTINGS_TYPE_SECURE,
                            owningUserId, name, forceNotify);
                }

                case MUTATION_OPERATION_UPDATE: {
                    return mSettingsRegistry.updateSettingLocked(SETTINGS_TYPE_SECURE,
                            owningUserId, name, value, getCallingPackage(), forceNotify);
                }
            }
        }

        return false;
    }
}
