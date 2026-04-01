class reset {
private void reset() {
            aad = null;
            final int lastBufSize = lastGlobalMessageSize;
            if (buf == null) {
                buf = new byte[lastBufSize];
            } else if (bufCount > 0 && bufCount != lastBufSize) {
                lastGlobalMessageSize = bufCount;
                if (buf.length != bufCount) {
                    buf = new byte[bufCount];
                }
            }
            bufCount = 0;
        }
}
