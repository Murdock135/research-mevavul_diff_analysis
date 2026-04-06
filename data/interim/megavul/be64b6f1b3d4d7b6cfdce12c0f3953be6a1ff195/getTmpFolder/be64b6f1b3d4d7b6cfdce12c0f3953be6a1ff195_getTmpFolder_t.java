class getTmpFolder {
private static File getTmpFolder() {
        try {
            File outputFolder = Files.createTempFile("codegen-", "-tmp").toFile();
            outputFolder.delete();
            outputFolder.mkdir();
            outputFolder.deleteOnExit();
            return outputFolder;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot access tmp folder");
        }
    }
}
