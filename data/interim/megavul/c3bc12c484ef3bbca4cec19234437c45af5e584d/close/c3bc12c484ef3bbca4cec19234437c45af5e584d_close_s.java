class close {
@Override
    public void close() {
        synchronized (this) {
            if (mOpen) {
                mOpen = false;

                if (mOwnsNative) {
                    nativeDestroy(mNative);
                }
            }
        }
    }
}
