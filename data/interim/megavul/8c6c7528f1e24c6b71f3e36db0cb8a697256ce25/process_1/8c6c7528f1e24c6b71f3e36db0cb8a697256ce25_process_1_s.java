class process_1 {
@Override
  protected byte[] process(final CiphertextHeader header, final boolean mode, final byte[] input)
  {
    final BlockCipherAdapter cipher = newCipher(header, mode);
    final byte[] headerBytes = header.encode();
    int outOff;
    final int inOff;
    final int length;
    final byte[] output;
    if (mode) {
      final int outSize = headerBytes.length + cipher.getOutputSize(input.length);
      output = new byte[outSize];
      System.arraycopy(headerBytes, 0, output, 0, headerBytes.length);
      inOff = 0;
      outOff = headerBytes.length;
      length = input.length;
    } else {
      length = input.length - headerBytes.length;

      final int outSize = cipher.getOutputSize(length);
      output = new byte[outSize];
      inOff = headerBytes.length;
      outOff = 0;
    }
    outOff += cipher.processBytes(input, inOff, length, output, outOff);
    outOff += cipher.doFinal(output, outOff);
    if (outOff < output.length) {
      final byte[] copy = new byte[outOff];
      System.arraycopy(output, 0, copy, 0, outOff);
      return copy;
    }
    return output;
  }
}
