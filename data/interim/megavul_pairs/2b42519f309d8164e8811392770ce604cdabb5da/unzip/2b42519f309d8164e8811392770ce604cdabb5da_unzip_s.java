class unzip {
public static File unzip(File fileToUnzip, File destDir) throws IOException {
        FileUtils.deleteQuietly(destDir);
        IOUtils.createDir(destDir);
        destDir.deleteOnExit();

        File file;
        ZipFile zipFile = new ZipFile(fileToUnzip);
        Enumeration<?> zipEnumeration = zipFile.entries();
        while (zipEnumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) zipEnumeration.nextElement();
            String fileName = zipEntry.getName();
            file = new File(destDir, fileName);
            if (fileName.endsWith(ZIP_FILE_SEPARATOR)) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            InputStream inputStream = zipFile.getInputStream(zipEntry);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[BUFFER_SIZE];
            int length;
            while ((length = inputStream.read(bytes)) >= 0) {
                fileOutputStream.write(bytes, 0, length);
            }
            inputStream.close();
            fileOutputStream.close();
        }
        zipFile.close();
        return destDir;
    }
}
