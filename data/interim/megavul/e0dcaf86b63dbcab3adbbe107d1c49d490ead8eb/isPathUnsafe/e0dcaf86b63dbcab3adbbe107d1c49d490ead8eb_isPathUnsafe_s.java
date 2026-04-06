class isPathUnsafe {
public static boolean isPathUnsafe(String path) {
        // Check that the path does not have '/../', '\..\', %5C..%5C, or
        // %2F..%2F
        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("An error occurred during decoding URL.",
                    e);
        }
        return PARENT_DIRECTORY_REGEX.matcher(path).find();
    }
}
