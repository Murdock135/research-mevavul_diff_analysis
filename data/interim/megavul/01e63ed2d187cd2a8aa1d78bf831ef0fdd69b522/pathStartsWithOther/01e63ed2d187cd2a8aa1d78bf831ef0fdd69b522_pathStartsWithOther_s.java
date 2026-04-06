class pathStartsWithOther {
private static boolean pathStartsWithOther(Path resolvedPath, Path basePath) throws IOException {
        try {
            return resolvedPath.toRealPath().startsWith(basePath.toRealPath());
        } catch (Exception e) {
            if (e instanceof NoSuchFileException) { // If we're about to creating a file this exception has been thrown
                return resolvedPath.normalize().startsWith(basePath);
            }
            return false;
        }
    }
}
