class isMetaInfFile {
static boolean isMetaInfFile(String name) {
        if (name.endsWith("class")) {
            return false;
        }
        return name.startsWith(META_INF) && (
                name.endsWith(".MF") ||
                name.endsWith(".SF") ||
                name.endsWith(".DSA") ||
                name.endsWith(".RSA") ||
                SIG.matcher(name).matches()
        );
    }
}
