class ensureOpenLocked {
@GuardedBy("this")
    private void ensureOpenLocked() {
        // If mOpen is true, this implies that mObject != 0.
        if (!mOpen) {
            throw new RuntimeException("AssetManager has been closed");
        }
    }
}
