class deleteFile {
@DELETE
    @Path("/contents")
    @Produces(MediaType.TEXT_HTML)
    public String deleteFile(@QueryParam("f") String fileName,
                             @Context SecurityContext securityContext) throws IOException {
        if (!securityContext.isUserInRole(Authentication.ROLE_FILESYSTEM_EDITOR)) {
            throw new ForbiddenException("FILESYSTEM EDITOR role is required for deleting file contents.");
        }
        final java.nio.file.Path targetPath = ensureFileIsAllowed(fileName, securityContext);
        Files.delete(targetPath);
        return String.format("Successfully deleted to '%s'.", targetPath);
    }
}
