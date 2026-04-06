class generateNonceIVPersonalizationString {
private static byte[] generateNonceIVPersonalizationString()
    {
        return Arrays.concatenate(Strings.toByteArray("Default"), Strings.toUTF8ByteArray(getVIMID()),
            Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }
}
