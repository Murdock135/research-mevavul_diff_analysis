class onPackageInactiveLocked {
@GuardedBy("mInnerLock")
        private void onPackageInactiveLocked() {
            if (mIsFinished) {
                return;
            }
            if (DEBUG) {
                Log.d(LOG_TAG, "onPackageInactiveLocked stack trace for "
                        + mPackageName + " (" + mUid + ").", new RuntimeException());
            }
            mIsFinished = true;
            cancelAlarmLocked();
            mHandler.post(
                    () -> {
                        Log.i(LOG_TAG, "One time session expired for "
                                + mPackageName + " (" + mUid + ").");

                        mPermissionControllerManager.notifyOneTimePermissionSessionTimeout(
                                mPackageName);
                    });
            mActivityManager.removeOnUidImportanceListener(mStartTimerListener);
            mActivityManager.removeOnUidImportanceListener(mSessionKillableListener);
            mActivityManager.removeOnUidImportanceListener(mGoneListener);
            synchronized (mLock) {
                mListeners.remove(mUid);
            }
        }
}
