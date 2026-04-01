class _finishBytes {
@SuppressWarnings("resource")
    protected byte[] _finishBytes(int len) throws IOException
    {
        // Chunked?
        // First, simple: non-chunked
        if (len <= 0) {
            if (len == 0) {
                return NO_BYTES;
            }
            return _finishChunkedBytes();
        }
        // Non-chunked, contiguous
        if (len > LONGEST_NON_CHUNKED_BINARY) {
            // [dataformats-binary#186]: avoid immediate allocation for longest
            return _finishLongContiguousBytes(len);
        }

        final byte[] b = new byte[len];
        final int expLen = len;
        if (_inputPtr >= _inputEnd) {
            if (!loadMore()) {
                _reportIncompleteBinaryRead(expLen, 0);
            }
        }

        int ptr = 0;
        while (true) {
            int toAdd = Math.min(len, _inputEnd - _inputPtr);
            System.arraycopy(_inputBuffer, _inputPtr, b, ptr, toAdd);
            _inputPtr += toAdd;
            ptr += toAdd;
            len -= toAdd;
            if (len <= 0) {
                return b;
            }
            if (!loadMore()) {
                _reportIncompleteBinaryRead(expLen, ptr);
            }
        }
    }
}
