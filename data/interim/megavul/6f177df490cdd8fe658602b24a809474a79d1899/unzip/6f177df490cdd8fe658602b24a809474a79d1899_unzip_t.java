class unzip {
public static void unzip(String zipFilePath, String targetPath, boolean overwrite) throws IOException {
        ZipFile zipFile = new ZipFile(zipFilePath, ENCODING);
        Enumeration<? extends ZipEntry> entryEnum = zipFile.getEntries();
        if (null != entryEnum) {
            while (entryEnum.hasMoreElements()) {
                ZipEntry zipEntry = entryEnum.nextElement();
                String filePath = zipEntry.getName();
                if (filePath.contains("..")) {
                    filePath = filePath.replace("..", Constants.BLANK);
                }
                if (zipEntry.isDirectory()) {
                    File dir = new File(targetPath + File.separator + filePath);
                    dir.mkdirs();
                } else {
                    File targetFile = new File(targetPath + File.separator + filePath);
                    if (!targetFile.exists() || overwrite) {
                        targetFile.getParentFile().mkdirs();
                        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
                                FileOutputStream outputStream = new FileOutputStream(targetFile);
                                FileLock fileLock = outputStream.getChannel().tryLock();) {
                            if (null != fileLock) {
                                StreamUtils.copy(inputStream, outputStream);
                            }
                        }
                    }
                }
            }
        }
        zipFile.close();
    }
}
