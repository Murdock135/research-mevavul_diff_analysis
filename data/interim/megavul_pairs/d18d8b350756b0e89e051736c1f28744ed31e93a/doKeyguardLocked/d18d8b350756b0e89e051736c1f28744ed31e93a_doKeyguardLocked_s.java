class doKeyguardLocked {
private void doKeyguardLocked(Bundle options) {
        if (KeyguardUpdateMonitor.CORE_APPS_ONLY) {
            // Don't show keyguard during half-booted cryptkeeper stage.
            if (DEBUG) Log.d(TAG, "doKeyguard: not showing because booting to cryptkeeper");
            return;
        }

        // if another app is disabling us, don't show
        if (!mExternallyEnabled) {
            if (DEBUG) Log.d(TAG, "doKeyguard: not showing because externally disabled");

            mNeedToReshowWhenReenabled = true;
            return;
        }

        // if the keyguard is already showing, don't bother. check flags in both files
        // to account for the hiding animation which results in a delay and discrepancy
        // between flags
        if (mShowing && mKeyguardViewControllerLazy.get().isShowing()) {
            if (DEBUG) Log.d(TAG, "doKeyguard: not showing because it is already showing");
            resetStateLocked();
            return;
        }

        // In split system user mode, we never unlock system user.
        if (!mustNotUnlockCurrentUser()
                || !mUpdateMonitor.isDeviceProvisioned()) {

            // if the setup wizard hasn't run yet, don't show
            final boolean requireSim = !SystemProperties.getBoolean("keyguard.no_require_sim", false);
            final boolean absent = SubscriptionManager.isValidSubscriptionId(
                    mUpdateMonitor.getNextSubIdForState(TelephonyManager.SIM_STATE_ABSENT));
            final boolean disabled = SubscriptionManager.isValidSubscriptionId(
                    mUpdateMonitor.getNextSubIdForState(TelephonyManager.SIM_STATE_PERM_DISABLED));
            final boolean lockedOrMissing = mUpdateMonitor.isSimPinSecure()
                    || ((absent || disabled) && requireSim);

            if (!lockedOrMissing && shouldWaitForProvisioning()) {
                if (DEBUG) Log.d(TAG, "doKeyguard: not showing because device isn't provisioned"
                        + " and the sim is not locked or missing");
                return;
            }

            boolean forceShow = options != null && options.getBoolean(OPTION_FORCE_SHOW, false);
            if (mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())
                    && !lockedOrMissing && !forceShow) {
                if (DEBUG) Log.d(TAG, "doKeyguard: not showing because lockscreen is off");
                return;
            }
        }

        if (DEBUG) Log.d(TAG, "doKeyguard: showing the lock screen");
        showLocked(options);
    }
}
