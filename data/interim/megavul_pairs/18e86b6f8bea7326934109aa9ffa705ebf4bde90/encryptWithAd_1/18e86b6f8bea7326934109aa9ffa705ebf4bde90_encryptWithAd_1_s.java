class encryptWithAd_1 {
@Override
	public int encryptWithAd(byte[] ad, byte[] plaintext, int plaintextOffset,
			byte[] ciphertext, int ciphertextOffset, int length) throws ShortBufferException {
		int space;
		if (ciphertextOffset > ciphertext.length)
			space = 0;
		else
			space = ciphertext.length - ciphertextOffset;
		if (!haskey) {
			// The key is not set yet - return the plaintext as-is.
			if (length > space)
				throw new ShortBufferException();
			if (plaintext != ciphertext || plaintextOffset != ciphertextOffset)
				System.arraycopy(plaintext, plaintextOffset, ciphertext, ciphertextOffset, length);
			return length;
		}
		if (space < 16 || length > (space - 16))
			throw new ShortBufferException();
		setup(ad);
		encrypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset, length);
		poly.update(ciphertext, ciphertextOffset, length);
		finish(ad, length);
		System.arraycopy(polyKey, 0, ciphertext, ciphertextOffset + length, 16);
		return length + 16;
	}
}
