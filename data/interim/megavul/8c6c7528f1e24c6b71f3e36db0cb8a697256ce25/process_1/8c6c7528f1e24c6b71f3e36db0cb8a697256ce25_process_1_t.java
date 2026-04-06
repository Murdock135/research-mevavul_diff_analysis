class process_1 {
@Override
  protected byte[] process(final CiphertextHeader header, final boolean mode, final byte[] input)
  {
    final BlockCipherAdapter cipher = newCipher(header, mode);
    int outOff;
    final int inOff;
    final int length;
    final byte[] output;
    if (mode) {
      final byte[] headerBytes = header.encode();
      final int outSize = headerBytes.length + cipher.getOutputSize(input.length);
      output = new byte[outSize];
      System.arraycopy(headerBytes, 0, output, 0, headerBytes.length);
      inOff = 0;
      outOff = headerBytes.length;
      length = input.length;
    } else {
      outOff = 0;
      inOff = header.getLength();
      length = input.length - inOff;

      final int outSize = cipher.getOutputSize(length);
      output = new byte[outSize];
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
