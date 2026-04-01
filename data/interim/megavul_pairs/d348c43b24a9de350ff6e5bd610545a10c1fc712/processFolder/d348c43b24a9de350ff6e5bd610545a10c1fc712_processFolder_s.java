class processFolder {
private static void processFolder(final File folder, final ZipOutputStream zipOutputStream, final int prefixLength)
            throws BrutException, IOException {
        for (final File file : folder.listFiles()) {
            if (file.isFile()) {
                final String cleanedPath = BrutIO.sanitizeUnknownFile(folder, file.getPath().substring(prefixLength));
                final ZipEntry zipEntry = new ZipEntry(BrutIO.normalizePath(cleanedPath));

                // aapt binary by default takes in parameters via -0 arsc to list extensions that shouldn't be
                // compressed. We will replicate that behavior
                final String extension = FilenameUtils.getExtension(file.getAbsolutePath());
                if (mDoNotCompress != null && (mDoNotCompress.contains(extension) || mDoNotCompress.contains(zipEntry.getName()))) {
                    zipEntry.setMethod(ZipEntry.STORED);
                    zipEntry.setSize(file.length());
                    BufferedInputStream unknownFile = new BufferedInputStream(Files.newInputStream(file.toPath()));
                    CRC32 crc = BrutIO.calculateCrc(unknownFile);
                    zipEntry.setCrc(crc.getValue());
                    unknownFile.close();
                } else {
                    zipEntry.setMethod(ZipEntry.DEFLATED);
                }

                zipOutputStream.putNextEntry(zipEntry);
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    IOUtils.copy(inputStream, zipOutputStream);
                }
                zipOutputStream.closeEntry();
            } else if (file.isDirectory()) {
                processFolder(file, zipOutputStream, prefixLength);
            }
        }
    }
}
