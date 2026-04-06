class decryptWithAd {
@Override
	public int decryptWithAd(byte[] ad, byte[] ciphertext,
			int ciphertextOffset, byte[] plaintext, int plaintextOffset,
			int length) throws ShortBufferException, BadPaddingException {
		int space;
		if (ciphertextOffset < 0 || ciphertextOffset > ciphertext.length)
			throw new IllegalArgumentException();
		else
			space = ciphertext.length - ciphertextOffset;
		if (length > space)
			throw new ShortBufferException();
		if (length < 0 || plaintextOffset < 0 || plaintextOffset > plaintext.length)
			throw new IllegalArgumentException();
		space = plaintext.length - plaintextOffset;
		if (keySpec == null) {
			// The key is not set yet - return the ciphertext as-is.
			if (length > space)
				throw new ShortBufferException();
			if (plaintext != ciphertext || plaintextOffset != ciphertextOffset)
				System.arraycopy(ciphertext, ciphertextOffset, plaintext, plaintextOffset, length);
			return length;
		}
		if (length < 16)
			Noise.throwBadTagException();
		int dataLen = length - 16;
		if (dataLen > space)
			throw new ShortBufferException();
		try {
			setup(ad);
		} catch (InvalidKeyException e) {
			// Shouldn't happen.
			throw new IllegalStateException(e);
		} catch (InvalidAlgorithmParameterException e) {
			// Shouldn't happen.
			throw new IllegalStateException(e);
		}
		ghash.update(ciphertext, ciphertextOffset, dataLen);
		ghash.pad(ad != null ? ad.length : 0, dataLen);
		ghash.finish(iv, 0, 16);
		int temp = 0;
		for (int index = 0; index < 16; ++index)
			temp |= (hashKey[index] ^ iv[index] ^ ciphertext[ciphertextOffset + dataLen + index]);
		if ((temp & 0xFF) != 0)
			Noise.throwBadTagException();
		try {
			int result = cipher.update(ciphertext, ciphertextOffset, dataLen, plaintext, plaintextOffset);
			cipher.doFinal(plaintext, plaintextOffset + result);
		} catch (IllegalBlockSizeException e) {
			// Shouldn't happen.
			throw new IllegalStateException(e);
		} catch (BadPaddingException e) {
			// Shouldn't happen.
			throw new IllegalStateException(e);
		}
		return dataLen;
	}
}
