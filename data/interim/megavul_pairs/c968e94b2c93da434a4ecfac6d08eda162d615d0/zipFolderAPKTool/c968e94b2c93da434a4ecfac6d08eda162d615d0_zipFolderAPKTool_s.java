class zipFolderAPKTool {
public static void zipFolderAPKTool(String srcFolder, String destZipFile) throws Exception {
        try (FileOutputStream fileWriter = new FileOutputStream(destZipFile);
             ZipOutputStream zip = new ZipOutputStream(fileWriter)){
            addFolderToZipAPKTool("", srcFolder, zip);
            zip.flush();
        }
    }
}
