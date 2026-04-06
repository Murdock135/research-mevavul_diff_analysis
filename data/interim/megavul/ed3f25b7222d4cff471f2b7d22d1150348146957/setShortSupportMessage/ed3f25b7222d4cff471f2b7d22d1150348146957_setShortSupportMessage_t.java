class setShortSupportMessage {
@Override
    public void setShortSupportMessage(@NonNull ComponentName who, CharSequence message) {
        if (!mHasFeature) {
            return;
        }
        Objects.requireNonNull(who, "ComponentName is null");
        message = truncateIfLonger(message, MAX_SHORT_SUPPORT_MESSAGE_LENGTH);

        final CallerIdentity caller = getCallerIdentity(who);
        synchronized (getLockObject()) {
            ActiveAdmin admin = getActiveAdminForUidLocked(who, caller.getUid());
            if (!TextUtils.equals(admin.shortSupportMessage, message)) {
                admin.shortSupportMessage = message;
                saveSettingsLocked(caller.getUserId());
            }
        }
        DevicePolicyEventLogger
                .createEvent(DevicePolicyEnums.SET_SHORT_SUPPORT_MESSAGE)
                .setAdmin(who)
                .write();
    }
}
