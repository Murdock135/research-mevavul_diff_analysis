class getCurrentState {
private int getCurrentState() {
            return getStateFromProcState(mActivityManagerInternal.getUidProcessState(mUid));
        }
}
