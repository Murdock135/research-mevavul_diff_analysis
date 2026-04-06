class getFiles {
@GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getFiles(@QueryParam("changedFilesOnly") boolean changedFilesOnly, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole(Authentication.ROLE_FILESYSTEM_EDITOR)) {
            throw new ForbiddenException("FILESYSTEM EDITOR role is required for enumerating files.");
        }

        try {
            return Files.find(etcFolder, 4, (path, basicFileAttributes) -> isSupportedExtension(path), FileVisitOption.FOLLOW_LINKS)
                    .filter(p -> !p.equals(usersXml) || securityContext.isUserInRole(Authentication.ROLE_ADMIN))
                    .map(p -> etcFolder.relativize(p).toString())
                    .filter(p -> !changedFilesOnly || !doesFileExistAndMatchContentsWithEtcPristine(p, securityContext))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to enumerate files in path: " + etcFolder, e);
        }
    }
}
