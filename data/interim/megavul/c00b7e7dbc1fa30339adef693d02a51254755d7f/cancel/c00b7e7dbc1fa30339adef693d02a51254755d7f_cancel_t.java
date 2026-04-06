class cancel {
private void cancel() {
            synchronized (mInnerLock) {
                mIsFinished = true;
                cancelAlarmLocked();
                try {
                    mIActivityManager.unregisterUidObserver(mObserver);
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Unable to unregister uid observer.", e);
                }
            }
        }
}
