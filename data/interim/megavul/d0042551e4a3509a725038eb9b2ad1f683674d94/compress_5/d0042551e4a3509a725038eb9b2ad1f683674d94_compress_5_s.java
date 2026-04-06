class compress_5 {
public static byte[] compress(int[] input)
            throws IOException
    {
        return rawCompress(input, input.length * 4); // int uses 4 bytes
    }
}
