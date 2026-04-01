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
        path.append(location.getPath().replace('/', File.separatorChar));
        if (location.getQuery() != null && !location.getQuery().trim().isEmpty()) {
            path.append(".").append(location.getQuery());
        }

        File candidate = new File(FileUtils.sanitizePath(path.toString()));
        if (candidate.getName().length() > 255) {
            /**
             * When filename is longer then 255 chars, then then various
             * filesystems have issues to save it. By saving the file by its
             * sum, we are trying to prevent collision of two files differs in
             * suffixes (general suffix of name, not only 'filetype suffix')
             * only. It is also preventing bug when truncate (files with 1000
             * chars hash in query) cuts to much.
             */
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] sum = md.digest(candidate.getName().getBytes(UTF_8));
                //convert the byte to hex format method 2
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < sum.length; i++) {
                    hexString.append(Integer.toHexString(0xFF & sum[i]));
                }
                String extension = "";
                int i = candidate.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = candidate.getName().substring(i);//contains dot
                }
                if (extension.length() < 10 && extension.length() > 1) {
                    hexString.append(extension);
                }
                candidate = new File(candidate.getParentFile(), hexString.toString());
            } catch (NoSuchAlgorithmException ex) {
                // should not occur, cite from javadoc:
                // every java implementation should support
                // MD5 SHA-1 SHA-256
                throw new RuntimeException(ex);
            }
        }
        return candidate;
    }
}
