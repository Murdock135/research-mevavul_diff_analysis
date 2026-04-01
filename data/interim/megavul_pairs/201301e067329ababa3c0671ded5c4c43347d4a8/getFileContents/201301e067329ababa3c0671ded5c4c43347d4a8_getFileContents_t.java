class getFileContents {
@GET
    @Path("/contents")
    public Response getFileContents(@QueryParam("f") String fileName, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole(Authentication.ROLE_FILESYSTEM_EDITOR)) {
            throw new ForbiddenException("FILESYSTEM EDITOR role is required for reading files.");
        }
        return fileContents(ensureFileIsAllowed(fileName, securityContext));
    }
}
