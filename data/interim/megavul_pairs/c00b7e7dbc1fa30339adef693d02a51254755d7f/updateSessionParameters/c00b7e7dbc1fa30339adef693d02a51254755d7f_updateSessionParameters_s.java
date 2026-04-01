class updateSessionParameters {
public void updateSessionParameters(long timeoutMillis, long revokeAfterKilledDelayMillis,
                int importanceToResetTimer, int importanceToKeepSessionAlive) {
            synchronized (mInnerLock) {
                mTimeout = Math.min(mTimeout, timeoutMillis);
                mRevokeAfterKilledDelay = Math.min(mRevokeAfterKilledDelay,
                        revokeAfterKilledDelayMillis == -1
                                ? DeviceConfig.getLong(
                                DeviceConfig.NAMESPACE_PERMISSIONS,
                                PROPERTY_KILLED_DELAY_CONFIG_KEY, DEFAULT_KILLED_DELAY_MILLIS)
                                : revokeAfterKilledDelayMillis);
                mImportanceToResetTimer = Math.min(importanceToResetTimer, mImportanceToResetTimer);
                mImportanceToKeepSessionAlive = Math.min(importanceToKeepSessionAlive,
                        mImportanceToKeepSessionAlive);
                Log.v(LOG_TAG,
                        "Updated params for " + mPackageName + ". timeout=" + mTimeout
                                + " killedDelay=" + mRevokeAfterKilledDelay
                                + " importanceToResetTimer=" + mImportanceToResetTimer
                                + " importanceToKeepSessionAlive=" + mImportanceToKeepSessionAlive);
                onImportanceChanged(mUid, mActivityManager.getPackageImportance(mPackageName));
            }
        }
}
