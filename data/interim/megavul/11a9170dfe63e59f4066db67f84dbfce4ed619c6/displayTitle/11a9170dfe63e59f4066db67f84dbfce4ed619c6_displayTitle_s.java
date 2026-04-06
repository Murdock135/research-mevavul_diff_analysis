class displayTitle {
private XDOM displayTitle(DocumentModelBridge document, DocumentDisplayerParameters parameters)
    {
        // 1. Try to use the title provided by the user.
        String rawTitle = document.getTitle();
        if (!StringUtils.isEmpty(rawTitle)) {
            try {
                String title = rawTitle;
                // Evaluate the title only if the document has script rights, otherwise use the raw title.
                if (authorizationManager.hasAccess(Right.SCRIPT, document.getContentAuthorReference(),
                    document.getDocumentReference())) {
                    title = evaluateTitle(rawTitle, document.getDocumentReference(), parameters);
                }
                return parseTitle(title);
            } catch (Exception e) {
                logger.warn("Failed to interpret title of document [{}].", document.getDocumentReference(), e);
            }
        }

        // 2. Try to extract the title from the document content.
        if ("1".equals(this.xwikicfg.getProperty("xwiki.title.compatibility", "0"))) {
            try {
                XDOM title = extractTitleFromContent(document, parameters);
                if (title != null) {
                    return title;
                }
            } catch (Exception e) {
                logger.warn("Failed to extract title from content of document [{}].", document.getDocumentReference(),
                    e);
            }
        }

        // 3. The title was not specified or its evaluation failed. Use the document name as a fall-back.
        return getStaticTitle(document);
    }
}
