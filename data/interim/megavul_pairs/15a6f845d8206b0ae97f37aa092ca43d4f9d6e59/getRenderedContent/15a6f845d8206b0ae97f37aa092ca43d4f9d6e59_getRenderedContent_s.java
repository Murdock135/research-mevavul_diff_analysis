class getRenderedContent {
public String getRenderedContent(String text, Syntax sourceSyntaxId, Syntax targetSyntaxId,
        boolean restrictedTransformationContext, XWikiDocument sDocument, boolean isolated, XWikiContext context)
    {
        Map<String, Object> backup = null;

        getProgress().startStep(this, "document.progress.renderText",
            "Execute content [{}] in the context of document [{}]",
            StringUtils.substring(text, 0, 100) + (text.length() >= 100 ? "..." : ""), getDocumentReference());

        XWikiDocument currentSDocument = (XWikiDocument) context.get(CKEY_SDOC);
        try {
            // We have to render the given text in the context of this document. Check if this document is already
            // on the context (same Java object reference). We don't check if the document references are equal
            // because this document can have temporary changes that are not present on the context document even if
            // it has the same document reference.
            if (isolated && context.getDoc() != this) {
                backup = new HashMap<>();
                backupContext(backup, context);
                setAsContextDoc(context);
            }

            // Make sure to execute the document with the right of the provided sdocument's author
            if (sDocument != null) {
                context.put(CKEY_SDOC, sDocument);
            }

            // Reuse this document's reference so that the Velocity macro name-space is computed based on it.
            XWikiDocument fakeDocument = new XWikiDocument(getDocumentReference());
            fakeDocument.setSyntax(sourceSyntaxId);
            fakeDocument.setContent(text);

            // We don't let displayer take care of the context isolation because we don't want the fake document to be
            // context document
            return fakeDocument.display(targetSyntaxId, false, isolated, restrictedTransformationContext,
                false);
        } catch (Exception e) {
            // Failed to render for some reason. This method should normally throw an exception but this
            // requires changing the signature of calling methods too.
            LOGGER.warn("Failed to render content [{}]", text, e);
        } finally {
            if (backup != null) {
                restoreContext(backup, context);
            }
            context.put(CKEY_SDOC, currentSDocument);

            getProgress().endStep(this);
        }

        return "";
    }
}
