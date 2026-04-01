class isMetaInfFile {
static boolean isMetaInfFile(String name) {
        String ucName = name.toUpperCase();
        return ucName.startsWith(META_INF);
    }
}
