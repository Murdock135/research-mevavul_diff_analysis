class processSx {
private URL processSx(long id, String queryString, XWikiContext context,
        FilesystemExportContext exportContext) throws Exception
    {
        SxSource sxSource = null;

        // Check if we have the JAR_RESOURCE_REQUEST_PARAMETER parameter in the query string
        List<NameValuePair> params = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            if (param.getName().equals(JAR_RESOURCE_REQUEST_PARAMETER)) {
                sxSource = new SxResourceSource(param.getValue());
                break;
            }
        }

        if (sxSource == null) {
            sxSource = new SxDocumentSource(context, getExtensionType());
        }

        String content = getContent(sxSource, exportContext);

        // Write the content to file
        // We need a unique name for that SSX content
        String targetPath = String.format("%s/%s", getSxPrefix(), id);
        File targetDirectory = new File(exportContext.getExportDir(), targetPath);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        File targetLocation = File.createTempFile(getSxPrefix(), "." + getFileSuffix(), targetDirectory);
        FileUtils.writeStringToFile(targetLocation, content);

        // Rewrite the URL
        StringBuilder path = new StringBuilder("file://");

        // Adjust based on current document's location. We need to account for the fact that the current document
        // is stored in subdirectories inside the top level "pages" directory. Since the SX files are also in top
        // subdirectories, we need to compute the path to them.
        path.append(StringUtils.repeat("../", exportContext.getDocParentLevel()));

        path.append(getSxPrefix());
        path.append(URL_PATH_SEPARATOR);
        path.append(id);
        path.append(URL_PATH_SEPARATOR);
        path.append(encodeURLPart(targetLocation.getName()));

        return new URL(path.toString());
    }
}
