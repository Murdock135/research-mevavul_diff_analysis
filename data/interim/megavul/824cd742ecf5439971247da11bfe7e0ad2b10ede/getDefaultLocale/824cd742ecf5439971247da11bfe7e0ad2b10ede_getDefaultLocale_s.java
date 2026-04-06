class getDefaultLocale {
private Locale getDefaultLocale(Document document)
    {
        if (document.isTranslation()) {
            // The default locale field is not always set on document translations:
            //
            // * it is empty for translation pages created by the user because the save action doesn't set it and the
            // edit form doesn't include this field;
            // * it may be set for translation pages that are part of an XWiki extension because the XAR Maven plugin
            // used to build the extension has a rule to enforce it;
            //
            // So we should take the default locale from the original document.
            try {
                XWikiContext xcontext = this.xcontextProvider.get();
                return xcontext.getWiki().getDocument(document.getDocumentReference(), xcontext).getRealLocale();
            } catch (XWikiException e) {
                this.logger.warn("Failed to get the default locale from [{}]. Root cause is [{}].",
                    document.getDocumentReference(), ExceptionUtils.getRootCauseMessage(e));
                // Fall-back on the default locale specified on the translation page, which may not be accurate.
                return document.getDefaultLocale();
            }
        } else {
            return document.getRealLocale();
        }
    }
}
