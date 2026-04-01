class uploadAttachment {
@Override
    public XWikiAttachment uploadAttachment(DocumentReference documentReference, Part part)
        throws TemporaryAttachmentException
    {
        XWikiAttachment xWikiAttachment;
        long uploadMaxSize = getUploadMaxSize(documentReference);
        if (part.getSize() > uploadMaxSize) {
            throw new TemporaryAttachmentException(String.format(
                "The file size [%s] is larger than the upload max size [%s]", part.getSize(), uploadMaxSize));
        }
        TemporaryAttachmentSession temporaryAttachmentSession = getOrCreateSession();
        XWikiContext context = this.contextProvider.get();
        try {
            xWikiAttachment = new XWikiAttachment();
            xWikiAttachment.setFilename(part.getSubmittedFileName());
            xWikiAttachment.setContent(part.getInputStream());
            xWikiAttachment.setAuthorReference(context.getUserReference());
            temporaryAttachmentSession.addAttachment(documentReference, xWikiAttachment);
        } catch (IOException e) {
            throw new TemporaryAttachmentException("Error while reading the content of a request part", e);
        }
        return xWikiAttachment;
    }
}
