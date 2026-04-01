class getCurrentState {
private int getCurrentState() {
            try {
                return getStateFromProcState(mIActivityManager.getUidProcessState(mUid, null));
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Couldn't check uid proc state", e);
            }
            return STATE_GONE;
        }
}
