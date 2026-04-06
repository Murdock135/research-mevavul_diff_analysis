class render_1 {
public String render(String path, XWikiContext context) throws XWikiException, IOException
    {
        // This Action expects an incoming Entity URL of the type:
        // http://localhost:8080/xwiki/bin/skin/<path to resource on the filesystem, relative to the xwiki webapp>
        // Example 1 (fs skin file): .../bin/skin/skins/flamingo/style.css?...
        // Example 2 (fs resource file): .../bin/skin/resources/uicomponents/search/searchSuggest.css
        // Example 3 (wiki skin attachment or xproperty): .../bin/skin/XWiki/DefaultSkin/somefile.css
        //
        // TODO: The mapping to an Entity URL is hackish and needs to be fixed,
        // see https://jira.xwiki.org/browse/XWIKI-12449

        // Since we support Nested Spaces, these two examples will be mapped as the following Attachment References:
        // Example 1: skins.flamingo@style\.css
        // Example 2: resources.uicomponents.search@searchSuggest\.css
        // Example 3: XWiki.DefaultSkin@somefile\.css

        XWiki xwiki = context.getWiki();

        // Since skin paths usually contain the name of skin document, it is likely that the context document belongs to
        // the current skin.
        XWikiDocument doc = context.getDoc();

        // The base skin could be either a filesystem directory, or an xdocument.
        String baseskin = xwiki.getBaseSkin(context, true);
        XWikiDocument baseskindoc = xwiki.getDocument(baseskin, context);

        // The default base skin is always a filesystem directory.
        String defaultbaseskin = xwiki.getDefaultBaseSkin(context);

        LOGGER.debug("document: [{}] ; baseskin: [{}] ; defaultbaseskin: [{}]",
            new Object[] { doc.getDocumentReference(), baseskin, defaultbaseskin });

        // Since we don't know exactly what does the URL point at, meaning that we don't know where the skin identifier
        // ends and where the path to the file starts, we must try to split at every '/' character.
        int idx = path.lastIndexOf(DELIMITER);
        boolean found = false;
        while (idx > 0) {
            try {
                String filename = Util.decodeURI(path.substring(idx + 1), context);
                LOGGER.debug("Trying [{}]", filename);

                // Try on the current skin document.
                if (renderSkin(filename, doc, context)) {
                    found = true;
                    break;
                }

                // Try on the base skin document, if it is not the same as above.
                if (StringUtils.isNotEmpty(baseskin) && !doc.getName().equals(baseskin)) {
                    if (renderSkin(filename, baseskindoc, context)) {
                        found = true;
                        break;
                    }
                }

                // Try on the default base skin, if it wasn't already tested above.
                if (StringUtils.isNotEmpty(baseskin)
                    && !(doc.getName().equals(defaultbaseskin) || baseskin.equals(defaultbaseskin))) {
                    // defaultbaseskin can only be on the filesystem, so don't try to use it as a
                    // skin document.
                    if (renderFileFromFilesystem(getSkinFilePath(filename, defaultbaseskin), context)) {
                        found = true;
                        break;
                    }
                }

                // Try in the resources directory.
                if (renderFileFromFilesystem(getResourceFilePath(filename), context)) {
                    found = true;
                    break;
                }
            } catch (XWikiException ex) {
                if (ex.getCode() == XWikiException.ERROR_XWIKI_APP_SEND_RESPONSE_EXCEPTION) {
                    // This means that the response couldn't be sent, although the file was
                    // successfully found. Signal this further, and stop trying to render.
                    throw ex;
                }
                LOGGER.debug(String.valueOf(idx), ex);
            }
            idx = path.lastIndexOf(DELIMITER, idx - 1);
        }
        if (!found) {
            context.getResponse().setStatus(404);
            return DOCDOESNOTEXIST;
        }
        return null;
    }
}
