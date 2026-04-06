class _readAndWriteBytes {
private int _readAndWriteBytes(OutputStream out, final int total) throws IOException
    {
        int left = total;
        while (left > 0) {
            int avail = _inputEnd - _inputPtr;
            if (_inputPtr >= _inputEnd) {
                if (!loadMore()) {
                    _reportIncompleteBinaryRead(total, total-left);
                }
                avail = _inputEnd - _inputPtr;
            }
            int count = Math.min(avail, left);
            out.write(_inputBuffer, _inputPtr, count);
            _inputPtr += count;
            left -= count;
        }
        _tokenIncomplete = false;
        return total;
    }
}
