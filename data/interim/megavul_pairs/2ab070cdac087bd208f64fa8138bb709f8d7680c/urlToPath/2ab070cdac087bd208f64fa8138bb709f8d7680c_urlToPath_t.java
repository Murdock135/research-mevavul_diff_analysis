class urlToPath {
public static File urlToPath(URL location, String subdir) {
        if (subdir == null) {
            throw new NullPointerException();
        }

        StringBuilder path = new StringBuilder();

        path.append(subdir);
        path.append(File.separatorChar);

        path.append(location.getProtocol());
        path.append(File.separatorChar);
        path.append(location.getHost());
        path.append(File.separatorChar);
        /**
         * This is a bit of imprecise. The usage of default port would be
         * better, but it would cause terrible backward incompatibility.
         */
        if (location.getPort() > 0) {
            path.append(location.getPort());
            path.append(File.separatorChar);
        }
        String locationPath = location.getPath().replace('/', File.separatorChar);
        String query = "";
        if (location.getQuery() != null) {
            query = location.getQuery();
        }
        if (locationPath.contains("..") || query.contains("..")){
            try {
                /**
                 * if path contains .. then it can harm lcoal system
                 * So without mercy, hash it
                 */
                String hexed = hex(new File(locationPath).getName(), locationPath);
                return new File(path.toString(), hexed.toString());
            } catch (NoSuchAlgorithmException ex) {
                // should not occur, cite from javadoc:
                // every java implementation should support
                // MD5 SHA-1 SHA-256
                throw new RuntimeException(ex);
            }
        } else {
            path.append(locationPath);
            if (location.getQuery() != null && !location.getQuery().trim().isEmpty()) {
                path.append(".").append(location.getQuery());
            }

            File candidate = new File(FileUtils.sanitizePath(path.toString()));
            try {
                if (candidate.getName().length() > 255) {
                    /**
                     * When filename is longer then 255 chars, then then various
                     * filesystems have issues to save it. By saving the file by its
                     * sum, we are trying to prevent collision of two files differs in
                     * suffixes (general suffix of name, not only 'filetype suffix')
                     * only. It is also preventing bug when truncate (files with 1000
                     * chars hash in query) cuts to much.
                     */
                    String hexed = hex(candidate.getName(), candidate.getName());
                    candidate = new File(candidate.getParentFile(), hexed.toString());
                }
            } catch (NoSuchAlgorithmException ex) {
                // should not occur, cite from javadoc:
                // every java implementation should support
                // MD5 SHA-1 SHA-256
                throw new RuntimeException(ex);
            }
            return candidate;
        }
    }
}
