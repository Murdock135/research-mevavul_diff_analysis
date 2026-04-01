class zipFolder {
public static void zipFolder(String srcFolder, String destZipFile, String ignore) throws Exception {
        try (FileOutputStream fileWriter = new FileOutputStream(destZipFile);
             ZipOutputStream zip = new ZipOutputStream(fileWriter)) {
            addFolderToZip("", srcFolder, zip, ignore);
            zip.flush();
        }
    }
}
