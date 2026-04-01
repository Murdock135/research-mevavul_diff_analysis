class doesFileExistAndMatchContentsWithEtcPristine {
public boolean doesFileExistAndMatchContentsWithEtcPristine(String file, final SecurityContext securityContext) {
        final java.nio.file.Path etcPath = ensureFileIsAllowed(file, securityContext);
        final java.nio.file.Path etcPristinePath = etcPristineFolder.resolve(file);
        if (!Files.exists(etcPristinePath)) {
            return false;
        }

        try (Reader pathReader = Files.newBufferedReader(etcPath);
             Reader etcPristineReader = Files.newBufferedReader(etcPristinePath)) {
            return IOUtils.contentEqualsIgnoreEOL(pathReader, etcPristineReader);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
