class unzip {
public static void unzip(String zipFilePath, String targetPath, boolean overwrite) throws IOException {
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<? extends ZipEntry> entryEnum = zipFile.getEntries();
        if (null != entryEnum) {
            while (entryEnum.hasMoreElements()) {
                ZipEntry zipEntry = entryEnum.nextElement();
                String filePath = zipEntry.getName();
                if (filePath.contains("..")) {
                    filePath = filePath.replace("..", BLANK);
                }
                if (zipEntry.isDirectory()) {
                    File dir = new File(targetPath + File.separator + filePath);
                    dir.mkdirs();
                } else {
                    File targetFile = new File(targetPath + File.separator + filePath);
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
