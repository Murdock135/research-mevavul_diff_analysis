class getLockTimeout {
private long getLockTimeout(int userId) {
        // if the screen turned off because of timeout or the user hit the power button,
        // and we don't need to lock immediately, set an alarm
        // to enable it a bit later (i.e, give the user a chance
        // to turn the screen back on within a certain window without
        // having to unlock the screen)
        final ContentResolver cr = mContext.getContentResolver();

        // From SecuritySettings
        final long lockAfterTimeout = Settings.Secure.getIntForUser(cr,
                Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT,
                KEYGUARD_LOCK_AFTER_DELAY_DEFAULT, userId);

        // From DevicePolicyAdmin
        final long policyTimeout = mLockPatternUtils.getDevicePolicyManager()
                .getMaximumTimeToLock(null, userId);

        long timeout;

        if (policyTimeout <= 0) {
            timeout = lockAfterTimeout;
        } else {
            // From DisplaySettings
            long displayTimeout = Settings.System.getIntForUser(cr, SCREEN_OFF_TIMEOUT,
                    KEYGUARD_DISPLAY_TIMEOUT_DELAY_DEFAULT, userId);

            // policy in effect. Make sure we don't go beyond policy limit.
            displayTimeout = Math.max(displayTimeout, 0); // ignore negative values
            timeout = Math.min(policyTimeout - displayTimeout, lockAfterTimeout);
            timeout = Math.max(timeout, 0);
        }
        return timeout;
    }
}
