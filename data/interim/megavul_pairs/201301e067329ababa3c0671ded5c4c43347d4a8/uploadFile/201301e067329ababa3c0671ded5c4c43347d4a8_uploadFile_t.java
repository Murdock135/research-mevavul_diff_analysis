class uploadFile {
@POST
    @Path("/contents")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(@QueryParam("f") String fileName,
                             @Multipart("upload") Attachment attachment,
                             @Context SecurityContext securityContext) throws IOException {
        if (!securityContext.isUserInRole(Authentication.ROLE_FILESYSTEM_EDITOR)) {
            throw new ForbiddenException("FILESYSTEM EDITOR role is required for uploading file contents.");
        }
        final java.nio.file.Path targetPath = ensureFileIsAllowed(fileName, securityContext);

        // Write the contents a temporary file
        final File tempFile = File.createTempFile("upload-", targetPath.getFileName().toString());
        try {
            tempFile.deleteOnExit();
            final InputStream in = attachment.getObject(InputStream.class);
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Validate it
            maybeValidateXml(tempFile);

            // Copy it to the right place
            Files.copy(tempFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return String.format("Successfully wrote to '%s'.", targetPath);
        } finally {
            // Delete the temporary file
            if (!tempFile.delete()) {
                LOG.warn("Failed to delete temporary file '{}' when uploading contents for '{}'.", tempFile, targetPath);
            }
        }
    }
}
