class generateDefaultPersonalizationString {
private static byte[] generateDefaultPersonalizationString()
    {
        return Arrays.concatenate(Strings.toByteArray("Default"), Strings.toUTF8ByteArray(getVIMID()),
            Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }
}
