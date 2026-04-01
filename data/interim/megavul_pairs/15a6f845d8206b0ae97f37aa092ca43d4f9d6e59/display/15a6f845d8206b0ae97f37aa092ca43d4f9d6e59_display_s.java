class display {
private String display(Syntax targetSyntax, boolean executionContextIsolated, boolean transformationContextIsolated,
        boolean transformationContextRestricted, boolean translate) throws XWikiException
    {
        // Note: We are currently duplicating code from getRendered signature because some calling
        // code is expecting that the rendering will happen in the calling document's context and not in this
        // document's context. For example this is true for the Admin page, see
        // https://jira.xwiki.org/browse/XWIKI-4274 for more details.

        getProgress().startStep(this, "document.progress.render", "Render document [{}] in syntax [{}]",
            getDocumentReference(), targetSyntax);

        try {
            getProgress().pushLevelProgress(3, getDocumentReference());

            getProgress().startStep(getDocumentReference(), "document.progress.render.translatedcontent",
                "Get translated content");

            XWikiContext xcontext = getXWikiContext();

            XWikiDocument tdoc = translate ? getTranslatedDocument(xcontext) : this;
            String translatedContent = tdoc.getContent();

            getProgress().startStep(getDocumentReference(), "document.progress.render.cache",
                "Try to get content from the cache");

            String renderedContent = getRenderingCache().getRenderedContent(tdoc.getDocumentReferenceWithLocale(),
                translatedContent, xcontext);

            if (renderedContent == null) {
                getProgress().startStep(getDocumentReference(), "document.progress.render.execute", "Execute content");

                // Configure display
                DocumentDisplayerParameters parameters = new DocumentDisplayerParameters();
                parameters.setExecutionContextIsolated(executionContextIsolated);
                parameters.setTransformationContextIsolated(transformationContextIsolated);
                parameters.setTransformationContextRestricted(transformationContextRestricted);
                // Render the translated content (matching the current language) using this document's syntax.
                parameters.setContentTranslated(tdoc != this);
                parameters.setTargetSyntax(targetSyntax);

                // Execute display
                XDOM contentXDOM = getDocumentDisplayer().display(this, parameters);

                // Render the result
                renderedContent = renderXDOM(contentXDOM, targetSyntax);

                getRenderingCache().setRenderedContent(getDocumentReference(), translatedContent, renderedContent,
                    xcontext);
            }

            return renderedContent;
        } finally {
            getProgress().popLevelProgress(getDocumentReference());
            getProgress().endStep(this);
        }
    }
}
