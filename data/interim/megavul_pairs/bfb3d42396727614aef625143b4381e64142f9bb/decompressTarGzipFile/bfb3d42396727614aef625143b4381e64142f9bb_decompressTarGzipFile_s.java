class decompressTarGzipFile {
public static void decompressTarGzipFile(InputStream is, File dest) throws IOException {
        try (GzipCompressorInputStream gzi = new GzipCompressorInputStream(is);
                TarArchiveInputStream tis = new TarArchiveInputStream(gzi)) {
            ArchiveEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                String name = entry.getName().substring(entry.getName().indexOf('/') + 1);
                File file = new File(dest, name);
                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(file);
                } else {
                    File parentFile = file.getParentFile();
                    FileUtils.forceMkdir(parentFile);
                    try (OutputStream os = Files.newOutputStream(file.toPath())) {
                        IOUtils.copy(tis, os);
                    }
                }
            }
        }
    }
}
