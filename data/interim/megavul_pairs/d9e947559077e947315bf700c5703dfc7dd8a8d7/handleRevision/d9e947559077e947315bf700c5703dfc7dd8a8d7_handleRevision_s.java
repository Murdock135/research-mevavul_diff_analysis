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
            XWikiDocument rdoc =
                (!doc.getLocale().equals(tdoc.getLocale())) ? doc : context.getWiki().getDocument(doc, rev, context);

            XWikiDocument rtdoc =
                (doc.getLocale().equals(tdoc.getLocale())) ? rdoc : context.getWiki().getDocument(tdoc, rev, context);

            context.put("tdoc", rtdoc);
            context.put("cdoc", rdoc);
            context.put("doc", rdoc);
        }
    }
}
