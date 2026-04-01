class unzip {
public static void unzip(InputStream is, File dest) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(dest, entry.getName());
                File canonicalDestDir = dest.getCanonicalFile();
                File canonicalFile = file.getCanonicalFile();

                // Check for Zip Slip vulnerability
                if (!canonicalFile.getPath().startsWith(canonicalDestDir.getPath())) {
                    throw new IOException("Detected Zip Slip vulnerability: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(file);
                } else {
                    File parentFile = file.getParentFile();
                    FileUtils.forceMkdir(parentFile);
                    try (OutputStream os = Files.newOutputStream(file.toPath())) {
                        IOUtils.copy(zis, os);
                    }
                }
            }
        }
    }
}
