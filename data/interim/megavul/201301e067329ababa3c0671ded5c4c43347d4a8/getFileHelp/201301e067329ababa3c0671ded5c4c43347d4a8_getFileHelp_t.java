class getFileHelp {
@GET
    @Path("/help")
    @Produces("text/markdown")
    public InputStream getFileHelp(@QueryParam("f") String fileName, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole(Authentication.ROLE_FILESYSTEM_EDITOR)) {
            throw new ForbiddenException("FILESYSTEM EDITOR role is required for retrieving help.");
        }
        ensureFileIsAllowed(fileName, securityContext);
        return this.getClass().getResourceAsStream("/help/" + fileName + ".md");
    }
}
