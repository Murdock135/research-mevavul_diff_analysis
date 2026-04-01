class generateNonceIVPersonalizationString {
private static byte[] generateNonceIVPersonalizationString(SecureRandom random)
    {
        return Arrays.concatenate(Strings.toByteArray("Nonce"), random.generateSeed(16),
            Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }
}
