class setOrganizationName {
@Override
    public void setOrganizationName(@NonNull ComponentName who, CharSequence text) {
        if (!mHasFeature) {
            return;
        }
        Objects.requireNonNull(who, "ComponentName is null");
        final CallerIdentity caller = getCallerIdentity(who);

        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerOrDeviceOwnerLocked(caller);
            if (!TextUtils.equals(admin.organizationName, text)) {
                admin.organizationName = (text == null || text.length() == 0)
                        ? null : text.toString();
                saveSettingsLocked(caller.getUserId());
            }
        }
    }
}
