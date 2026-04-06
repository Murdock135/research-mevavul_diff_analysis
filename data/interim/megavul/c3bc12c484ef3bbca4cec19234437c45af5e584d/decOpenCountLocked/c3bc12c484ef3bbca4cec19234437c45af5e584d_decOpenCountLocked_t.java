class decOpenCountLocked {
private void decOpenCountLocked() {
        mOpenCount--;
        if (mOpenCount == 0) {
            mStrings.close();
            nativeDestroy(mNative);
            mNative = 0;
            if (mAssets != null) {
                mAssets.xmlBlockGone(hashCode());
            }
        }
    }
}
