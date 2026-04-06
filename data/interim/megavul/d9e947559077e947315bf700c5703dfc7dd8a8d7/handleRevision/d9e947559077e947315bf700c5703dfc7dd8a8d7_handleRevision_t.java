class handleRevision {
protected void handleRevision(XWikiContext context) throws XWikiException
    {
        String rev = context.getRequest().getParameter("rev");
        if (rev != null) {
            context.put("rev", rev);
            XWikiDocument doc = (XWikiDocument) context.get("doc");
            XWikiDocument tdoc = (XWikiDocument) context.get("tdoc");
            // if the doc is deleted and we request a specific language, we have to set the locale so we can retrieve
            // properly the document revision.
            if (rev.startsWith("deleted") && !StringUtils.isEmpty(context.getRequest().getParameter("language"))
                && doc == tdoc) {
                Locale locale = LocaleUtils.toLocale(context.getRequest().getParameter("language"), Locale.ROOT);
                tdoc = new XWikiDocument(tdoc.getDocumentReference(), locale);
            }

            DocumentReference documentReference = doc.getDocumentReference();
            try {
                documentRevisionProvider
                    .checkAccess(Right.VIEW, getCurrentUserReference(context), documentReference, rev);
            } catch (AuthorizationException e) {
                Object[] args = { documentReference, rev, context.getUserReference() };
                throw new XWikiException(XWikiException.MODULE_XWIKI_ACCESS, XWikiException.ERROR_XWIKI_ACCESS_DENIED,
                    "Access to document {0} with revision {1} has been denied to user {2}", e, args);
            }

            XWikiDocument rdoc;
            XWikiDocument rtdoc;
            if (doc.getLocale().equals(tdoc.getLocale())) {
                rdoc = this.documentRevisionProvider.getRevision(doc.getDocumentReferenceWithLocale(), rev);
                rtdoc = rdoc;
            } else {
                rdoc = doc;
                rtdoc = this.documentRevisionProvider.getRevision(tdoc.getDocumentReferenceWithLocale(), rev);
            }

            context.put("tdoc", rtdoc);
            context.put("cdoc", rdoc);
            context.put("doc", rdoc);
        }
    }
}
