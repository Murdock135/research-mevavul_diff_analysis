class generateDefaultPersonalizationString {
private static byte[] generateDefaultPersonalizationString(SecureRandom random)
    {
        return Arrays.concatenate(Strings.toByteArray("Default"), random.generateSeed(16),
            Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }
}
