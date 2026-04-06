class toByteArray {
public byte[] toByteArray()
    {
		/* index || secretKeySeed || secretKeyPRF || publicSeed || root */
        int n = params.getDigestSize();
        int indexSize = (params.getHeight() + 7) / 8;
        int secretKeySize = n;
        int secretKeyPRFSize = n;
        int publicSeedSize = n;
        int rootSize = n;
        int totalSize = indexSize + secretKeySize + secretKeyPRFSize + publicSeedSize + rootSize;
        byte[] out = new byte[totalSize];
        int position = 0;
		/* copy index */
        byte[] indexBytes = XMSSUtil.toBytesBigEndian(index, indexSize);
        XMSSUtil.copyBytesAtOffset(out, indexBytes, position);
        position += indexSize;
		/* copy secretKeySeed */
        XMSSUtil.copyBytesAtOffset(out, secretKeySeed, position);
        position += secretKeySize;
		/* copy secretKeyPRF */
        XMSSUtil.copyBytesAtOffset(out, secretKeyPRF, position);
        position += secretKeyPRFSize;
		/* copy publicSeed */
        XMSSUtil.copyBytesAtOffset(out, publicSeed, position);
        position += publicSeedSize;
		/* copy root */
        XMSSUtil.copyBytesAtOffset(out, root, position);
		/* concatenate bdsState */
        try
        {
            return Arrays.concatenate(out, XMSSUtil.serialize(bdsState));
        }
        catch (IOException e)
        {
            throw new IllegalStateException("error serializing bds state: " + e.getMessage(), e);
        }
    }
}
