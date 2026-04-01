class updateDocument {
@Override
    public boolean updateDocument(XWikiDocument document)
    {
        boolean needsUpdate = false;

        if (updateReferences(document)) {
            needsUpdate = true;
        }

        // Ensure the document is hidden, like every technical document
        if (!document.isHidden()) {
            document.setHidden(true);
            needsUpdate = true;
        }

        // Set the title
        if (document.getTitle().isEmpty()) {
            document.setTitle("Mail Configuration");
            needsUpdate = true;
        }

        // Ensure the document has a Mail.GeneralMailConfigClass xobject
        if (addGeneralMailConfigClassXObject(document)) {
            needsUpdate = true;
        }

        // Ensure the document has a Mail.SendMailConfigClass xobject
        if (addSendMailConfigClassXObject(document)) {
            needsUpdate = true;
        }

        // Migration: Since the XWiki.Configurable class used to be located in Mail.MailConfig and has been moved to
        // Mail.MailConfigurable, we need to remove it from this page in case it's there as otherwise there would be
        // duplicate Admin UI entries...
        // TODO: Ideally this code should be executed only once.
        if (removeConfigurableClass(document)) {
            needsUpdate = true;
        }

        return needsUpdate;
    }
}
