class extract {
public void extract() throws IOException {
        log.debug("Extract content of '{}' to '{}'", source, destination);

        // delete destination directory if exists
        if (destination.exists() && destination.isDirectory()) {
            FileUtils.delete(destination.toPath());
        }

        String destinationCanonicalPath = destination.getCanonicalPath();
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File file = new File(destination, zipEntry.getName());

                String fileCanonicalPath = file.getCanonicalPath();
                if (!fileCanonicalPath.startsWith(destinationCanonicalPath)) {
                    throw new ZipException("The file "+ zipEntry.getName() + " is trying to leave the target output directory of "+ destination);
                }

                // create intermediary directories - sometimes zip don't add them
                File dir = new File(file.getParent());

                mkdirsOrThrow(dir);

                if (zipEntry.isDirectory()) {
                    mkdirsOrThrow(file);
                } else {
                    byte[] buffer = new byte[1024];
                    int length;
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        while ((length = zipInputStream.read(buffer)) >= 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }
}
