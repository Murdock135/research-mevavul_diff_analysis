class setPermittedInputMethods {
@Override
    public boolean setPermittedInputMethods(ComponentName who, List<String> packageList,
            boolean calledOnParentInstance) {
        if (!mHasFeature) {
            return false;
        }
        Objects.requireNonNull(who, "ComponentName is null");

        final CallerIdentity caller = getCallerIdentity(who);
        final int userId = getProfileParentUserIfRequested(
                caller.getUserId(), calledOnParentInstance);
        if (calledOnParentInstance) {
            Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(caller));
            Preconditions.checkArgument(packageList == null || packageList.isEmpty(),
                    "Permitted input methods must allow all input methods or only "
                            + "system input methods when called on the parent instance of an "
                            + "organization-owned device");
        } else {
            Preconditions.checkCallAuthorization(
                    isDefaultDeviceOwner(caller) || isProfileOwner(caller));
        }

        if (packageList != null) {
            List<InputMethodInfo> enabledImes = mInjector.binderWithCleanCallingIdentity(() ->
                    InputMethodManagerInternal.get().getEnabledInputMethodListAsUser(userId));
            if (enabledImes != null) {
                List<String> enabledPackages = new ArrayList<String>();
                for (InputMethodInfo ime : enabledImes) {
                    enabledPackages.add(ime.getPackageName());
                }
                if (!checkPackagesInPermittedListOrSystem(enabledPackages, packageList,
                        userId)) {
                    Slogf.e(LOG_TAG, "Cannot set permitted input methods, because the list of "
                            + "permitted input methods excludes an already-enabled input method.");
                    return false;
                }
            }
        }

        synchronized (getLockObject()) {
            final ActiveAdmin admin = getParentOfAdminIfRequired(
                    getProfileOwnerOrDeviceOwnerLocked(caller), calledOnParentInstance);
            admin.permittedInputMethods = packageList;
            saveSettingsLocked(caller.getUserId());
        }

        DevicePolicyEventLogger
                .createEvent(DevicePolicyEnums.SET_PERMITTED_INPUT_METHODS)
                .setAdmin(who)
                .setStrings(getStringArrayForLogging(packageList, calledOnParentInstance))
                .write();
        return true;
    }
}
