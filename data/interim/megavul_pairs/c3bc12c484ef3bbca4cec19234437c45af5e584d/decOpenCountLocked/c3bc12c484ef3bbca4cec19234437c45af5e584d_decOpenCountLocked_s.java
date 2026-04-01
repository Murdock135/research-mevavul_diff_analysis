class decOpenCountLocked {
private void decOpenCountLocked() {
        mOpenCount--;
        if (mOpenCount == 0) {
            nativeDestroy(mNative);
            if (mAssets != null) {
                mAssets.xmlBlockGone(hashCode());
            }
        }
    }
}
