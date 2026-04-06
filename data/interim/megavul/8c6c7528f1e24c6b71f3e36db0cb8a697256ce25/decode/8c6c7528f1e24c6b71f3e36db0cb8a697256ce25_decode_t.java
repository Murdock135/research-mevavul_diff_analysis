class decode {
public static CiphertextHeader decode(final InputStream input) throws EncodingException, StreamException
  {
    final int length = ByteUtil.readInt(input);
    if (length < 0) {
      throw new EncodingException("Bad ciphertext header");
    }

    final byte[] nonce;
    int nonceLen = 0;
    try {
      nonceLen = ByteUtil.readInt(input);
      if (nonceLen > MAX_NONCE_LEN) {
        throw new EncodingException("Bad ciphertext header: maximum nonce size exceeded");
      }
      nonce = new byte[nonceLen];
      input.read(nonce);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new EncodingException("Bad ciphertext header");
    } catch (IOException e) {
      throw new StreamException(e);
    }

    String keyName = null;
    if (length > nonce.length + 8) {
      final byte[] b;
      int keyLen = 0;
      try {
        keyLen = ByteUtil.readInt(input);
        if (keyLen > MAX_KEYNAME_LEN) {
          throw new EncodingException("Bad ciphertext header: maximum key length exceeded");
        }
        b = new byte[keyLen];
        input.read(b);
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new EncodingException("Bad ciphertext header");
      } catch (IOException e) {
        throw new StreamException(e);
      }
      keyName = new String(b);
    }

    return new CiphertextHeader(nonce, keyName);
  }
}
