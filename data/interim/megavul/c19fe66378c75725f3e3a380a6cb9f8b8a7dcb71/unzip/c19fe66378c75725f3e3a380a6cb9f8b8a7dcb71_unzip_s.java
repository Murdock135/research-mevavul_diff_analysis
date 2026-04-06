class unzip {
public static void unzip(String zipFilePath, String targetPath, boolean overwrite) throws IOException {
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<? extends ZipEntry> entryEnum = zipFile.getEntries();
        if (null != entryEnum) {
            while (entryEnum.hasMoreElements()) {
                ZipEntry zipEntry = entryEnum.nextElement();
                if (zipEntry.isDirectory()) {
                    File dir = new File(targetPath + File.separator + zipEntry.getName());
                    dir.mkdirs();
                } else {
                    File targetFile = new File(targetPath + File.separator + zipEntry.getName());
                    if (!targetFile.exists() || overwrite) {
                        targetFile.getParentFile().mkdirs();
                        write(zipFile.getInputStream(zipEntry), new FileOutputStream(targetFile));
                    }
                }
            }
        }
        zipFile.close();
    }
}
