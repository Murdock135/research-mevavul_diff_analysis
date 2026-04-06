class render_1 {
@Override
    public String render(XWikiContext context) throws XWikiException
    {
        XWikiRequest request = context.getRequest();
        String content = request.getParameter("content");
        XWikiDocument doc = context.getDoc();
        XWikiForm form = context.getForm();

        synchronized (doc) {
            XWikiDocument tdoc = (XWikiDocument) context.get("tdoc");
            EditForm peform = (EditForm) form;
            String parent = peform.getParent();
            if (parent != null) {
                doc.setParent(parent);
            }
            String creator = peform.getCreator();
            if (creator != null) {
                doc.setCreator(creator);
            }
            String defaultTemplate = peform.getDefaultTemplate();
            if (defaultTemplate != null) {
                doc.setDefaultTemplate(defaultTemplate);
            }
            String defaultLanguage = peform.getDefaultLanguage();
            if ((defaultLanguage != null) && !defaultLanguage.equals("")) {
                doc.setDefaultLanguage(defaultLanguage);
            }
            if (doc.getDefaultLanguage().equals("")) {
                doc.setDefaultLanguage(context.getWiki().getLanguagePreference(context));
            }

            String language = context.getWiki().getLanguagePreference(context);
            String languagefromrequest = context.getRequest().getParameter("language");
            String languagetoedit =
                ((languagefromrequest == null) || (languagefromrequest.equals(""))) ? language : languagefromrequest;

            if ((languagetoedit == null) || (languagetoedit.equals("default"))) {
                languagetoedit = "";
            }
            if (doc.isNew() || (doc.getDefaultLanguage().equals(languagetoedit))) {
                languagetoedit = "";
            }

            if (languagetoedit.equals("")) {
                // In this case the created document is going to be the default document
                tdoc = doc;
                context.put("tdoc", doc);
                if (doc.isNew()) {
                    doc.setDefaultLanguage(language);
                    doc.setLanguage("");
                }
            } else {
                // If the translated doc object is the same as the doc object
                // this means the translated doc did not exists so we need to create it
                if ((tdoc == doc)) {
                    tdoc = new XWikiDocument(doc.getDocumentReference());
                    tdoc.setLanguage(languagetoedit);
                    tdoc.setContent(doc.getContent());
                    tdoc.setSyntax(doc.getSyntax());
                    tdoc.setAuthor(context.getUser());
                    tdoc.setStore(doc.getStore());
                    context.put("tdoc", tdoc);
                }
            }

            XWikiDocument tdoc2 = tdoc.clone();
            if (content != null && !content.isEmpty()) {
                tdoc2.setContent(content);
            }
            context.put("tdoc", tdoc2);
            try {
                readFromTemplate(tdoc2, peform.getTemplate(), context);
            } catch (XWikiException e) {
                if (e.getCode() == XWikiException.ERROR_XWIKI_APP_DOCUMENT_NOT_EMPTY) {
                    context.put("exception", e);
                    return "docalreadyexists";
                }
            }

            /* Setup a lock */
            try {
                XWikiLock lock = tdoc.getLock(context);
                if ((lock == null) || (lock.getUserName().equals(context.getUser())) || (peform.isLockForce())) {
                    tdoc.setLock(context.getUser(), context);
                }
            } catch (Exception e) {
                // Lock should never make XWiki fail
                // But we should log any related information
                LOGGER.error("Exception while setting up lock", e);
            }
        }

        return "admin";
    }
}
