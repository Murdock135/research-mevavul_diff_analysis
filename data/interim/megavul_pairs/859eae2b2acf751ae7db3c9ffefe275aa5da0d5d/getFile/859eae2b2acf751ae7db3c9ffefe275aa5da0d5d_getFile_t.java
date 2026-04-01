class getFile {
private File getFile(String... path) {
        String composedPath;
        if (path.length == 1) {
            composedPath = path[0];
        } else {
            // We need to ignore empty Strings as this is what "new File(parent, path)" was doing for "path" empty.
            composedPath = Arrays.stream(path)
                .filter(((Predicate<String>) String::isEmpty).negate())
                .collect(Collectors.joining(File.separator));
        }
        return new File(baseDir, ZipSlip.safeZipEntryName(trimLeadingSlash(composedPath)));
    }
}
