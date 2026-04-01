class setLongSupportMessage {
@Override
    public void setLongSupportMessage(@NonNull ComponentName who, CharSequence message) {
        if (!mHasFeature) {
            return;
        }
        Objects.requireNonNull(who, "ComponentName is null");
        final CallerIdentity caller = getCallerIdentity(who);
        synchronized (getLockObject()) {
            ActiveAdmin admin = getActiveAdminForUidLocked(who, caller.getUid());
            if (!TextUtils.equals(admin.longSupportMessage, message)) {
                admin.longSupportMessage = message;
                saveSettingsLocked(caller.getUserId());
            }
        }
        DevicePolicyEventLogger
                .createEvent(DevicePolicyEnums.SET_LONG_SUPPORT_MESSAGE)
                .setAdmin(who)
                .write();
    }
}
