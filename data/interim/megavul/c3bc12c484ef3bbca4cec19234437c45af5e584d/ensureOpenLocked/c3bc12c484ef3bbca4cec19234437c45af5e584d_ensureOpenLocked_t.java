class ensureOpenLocked {
@GuardedBy("this")
    private void ensureOpenLocked() {
        // If mOpen is true, this implies that mObject != 0.
        if (!mOpen) {
            throw new RuntimeException("AssetManager has been closed");
        }
        // Let's still check if the native object exists, given all the memory corruptions.
        if (mObject == 0) {
            throw new RuntimeException("AssetManager is open but the native object is gone");
        }
    }
}
