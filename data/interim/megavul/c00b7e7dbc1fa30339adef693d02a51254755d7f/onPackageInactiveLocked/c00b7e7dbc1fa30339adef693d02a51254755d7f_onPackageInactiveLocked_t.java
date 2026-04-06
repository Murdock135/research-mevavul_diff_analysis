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
            try {
                mIActivityManager.unregisterUidObserver(mObserver);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Unable to unregister uid observer.", e);
            }
            synchronized (mLock) {
                mListeners.remove(mUid);
            }
        }
}
