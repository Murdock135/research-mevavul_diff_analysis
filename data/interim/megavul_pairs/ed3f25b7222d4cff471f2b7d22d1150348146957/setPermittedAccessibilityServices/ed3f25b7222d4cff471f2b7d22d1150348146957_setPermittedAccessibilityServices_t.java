class setPermittedAccessibilityServices {
@Override
    public boolean setPermittedAccessibilityServices(ComponentName who, List<String> packageList) {
        if (!mHasFeature) {
            return false;
        }
        Objects.requireNonNull(who, "ComponentName is null");
        final CallerIdentity caller = getCallerIdentity(who);

        if (packageList != null) {
            for (String pkg : packageList) {
                enforceMaxPackageNameLength(pkg);
            }

            int userId = caller.getUserId();
            final List<AccessibilityServiceInfo> enabledServices;
            long id = mInjector.binderClearCallingIdentity();
            try {
                UserInfo user = getUserInfo(userId);
                if (user.isManagedProfile()) {
                    userId = user.profileGroupId;
                }
                enabledServices = withAccessibilityManager(userId,
                        am -> am.getEnabledAccessibilityServiceList(FEEDBACK_ALL_MASK));
            } finally {
                mInjector.binderRestoreCallingIdentity(id);
            }

            if (enabledServices != null) {
                List<String> enabledPackages = new ArrayList<>();
                for (AccessibilityServiceInfo service : enabledServices) {
                    enabledPackages.add(service.getResolveInfo().serviceInfo.packageName);
                }
                if (!checkPackagesInPermittedListOrSystem(enabledPackages, packageList,
                        userId)) {
                    Slogf.e(LOG_TAG, "Cannot set permitted accessibility services, "
                            + "because it contains already enabled accesibility services.");
                    return false;
                }
            }
        }

        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerOrDeviceOwnerLocked(caller);
            admin.permittedAccessiblityServices = packageList;
            saveSettingsLocked(UserHandle.getCallingUserId());
        }
        final String[] packageArray =
                packageList != null ? ((List<String>) packageList).toArray(new String[0]) : null;
        DevicePolicyEventLogger
                .createEvent(DevicePolicyEnums.SET_PERMITTED_ACCESSIBILITY_SERVICES)
                .setAdmin(who)
                .setStrings(packageArray)
                .write();
        return true;
    }
}
