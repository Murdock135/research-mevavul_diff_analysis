class _finishBytes {
@SuppressWarnings("resource")
    protected byte[] _finishBytes(int len) throws IOException
    {
        // First, simple: non-chunked
        if (len >= 0) {
            if (len == 0) {
                return NO_BYTES;
            }
            byte[] b = new byte[len];
            if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
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
                loadMoreGuaranteed();
            }
        }

        // or, if not, chunked...
        ByteArrayBuilder bb = _getByteArrayBuilder();
        while (true) {
            if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
            }
            int ch = _inputBuffer[_inputPtr++] & 0xFF;
            if (ch == 0xFF) { // end marker
                break;
            }
            // verify that type matches
            int type = (ch >> 5);
            if (type != CBORConstants.MAJOR_TYPE_BYTES) {
                throw _constructError("Mismatched chunk in chunked content: expected "+CBORConstants.MAJOR_TYPE_BYTES
                        +" but encountered "+type);
            }
            len = _decodeExplicitLength(ch & 0x1F);
            if (len < 0) {
                throw _constructError("Illegal chunked-length indicator within chunked-length value (type "+CBORConstants.MAJOR_TYPE_BYTES+")");
            }
            while (len > 0) {
                int avail = _inputEnd - _inputPtr;
                if (_inputPtr >= _inputEnd) {
                    loadMoreGuaranteed();
                    avail = _inputEnd - _inputPtr;
                }
                int count = Math.min(avail, len);
                bb.write(_inputBuffer, _inputPtr, count);
                _inputPtr += count;
                len -= count;
            }
        }
        return bb.toByteArray();
    }
}
