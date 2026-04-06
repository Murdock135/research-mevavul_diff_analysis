class cancel {
private void cancel() {
            synchronized (mInnerLock) {
                mIsFinished = true;
                cancelAlarmLocked();
                try {
                    mActivityManager.removeOnUidImportanceListener(mStartTimerListener);
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "Could not remove start timer listener", e);
                }
                try {
                    mActivityManager.removeOnUidImportanceListener(mSessionKillableListener);
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "Could not remove session killable listener", e);
                }
                try {
                    mActivityManager.removeOnUidImportanceListener(mGoneListener);
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "Could not remove gone listener", e);
                }
            }
        }
}
