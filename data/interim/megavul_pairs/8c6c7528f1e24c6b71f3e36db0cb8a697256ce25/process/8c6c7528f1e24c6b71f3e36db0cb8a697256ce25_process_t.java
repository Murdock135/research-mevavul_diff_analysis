class process {
@Override
  protected void process(
    final CiphertextHeader header,
    final boolean mode,
    final InputStream input,
    final OutputStream output)
  {
    final BlockCipherAdapter cipher = newCipher(header, mode);
    final int outSize = cipher.getOutputSize(StreamUtil.CHUNK_SIZE);
    final byte[] outBuf = new byte[Math.max(outSize, StreamUtil.CHUNK_SIZE)];
    StreamUtil.pipeAll(
      input,
      output,
      (in, inOff, len, out) -> {
        final int n = cipher.processBytes(in, inOff, len, outBuf, 0);
        out.write(outBuf, 0, n);
      });

    final int n = cipher.doFinal(outBuf, 0);
    try {
      output.write(outBuf, 0, n);
    } catch (IOException e) {
      throw new StreamException(e);
    }
  }
}
