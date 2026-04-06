class createBaloInHomeRepo {
private static void createBaloInHomeRepo(HttpURLConnection conn, String modulePathInBaloCache,
            String moduleNameWithOrg, boolean isNightlyBuild, String newUrl, String contentDisposition) {
        long responseContentLength = conn.getContentLengthLong();
        if (responseContentLength <= 0) {
            createError("invalid response from the server, please try again");
        }
        String resolvedURI = conn.getHeaderField(RESOLVED_REQUESTED_URI);
        if (resolvedURI == null || resolvedURI.equals("")) {
            resolvedURI = newUrl;
        }
        String[] uriParts = resolvedURI.split("/");
        String moduleVersion = uriParts[uriParts.length - 3];

        validateModuleVersion(moduleVersion);
        String baloFile = getBaloFileName(contentDisposition, uriParts[uriParts.length - 1]);
        Path baloCacheWithModulePath = Paths.get(modulePathInBaloCache, moduleVersion);
        //<user.home>.ballerina/balo_cache/<org-name>/<module-name>/<module-version>

        Path baloPath = Paths.get(baloCacheWithModulePath.toString(), baloFile);
        if (baloPath.toFile().exists()) {
            createError("module already exists in the home repository: " + baloPath.toString());
        }

        createBaloFileDirectory(baloCacheWithModulePath);
        writeBaloFile(conn, baloPath, moduleNameWithOrg + ":" + moduleVersion, responseContentLength);
        handleNightlyBuild(isNightlyBuild, baloCacheWithModulePath);
    }
}
