class copyToDir {
public static void copyToDir(Directory in, File out, String fileName)
            throws DirectoryException {
        try {
            if (in.containsDir(fileName)) {
                OS.rmdir(new File(out, fileName));
                in.getDir(fileName).copyToDir(new File(out, fileName));
            } else if (!in.containsDir(fileName) && !in.containsFile(fileName)) {
                // Skip copies of directories/files not found.
            } else {
                String cleanedFilename = BrutIO.sanitizeUnknownFile(out, fileName);
                if (! cleanedFilename.isEmpty()) {
                    File outFile = new File(out, cleanedFilename);
                    //noinspection ResultOfMethodCallIgnored
                    outFile.getParentFile().mkdirs();
                    BrutIO.copyAndClose(in.getFileInput(fileName), Files.newOutputStream(outFile.toPath()));
                }
            }
        } catch (FileSystemException exception) {
            LOGGER.warning(String.format("Skipping file %s (%s)", fileName, exception.getReason()));
        } catch (RootUnknownFileException | InvalidUnknownFileException | TraversalUnknownFileException | IOException exception) {
            LOGGER.warning(String.format("Skipping file %s (%s)", fileName, exception.getMessage()));
        } catch (BrutException ex) {
            throw new DirectoryException("Error copying file: " + fileName, ex);
        }
    }
}
