class uploadTemporaryAttachment {
public Attachment uploadTemporaryAttachment(DocumentReference documentReference, String fieldName)
    {
        XWikiContext context = this.contextProvider.get();
        Optional<XWikiAttachment> result = Optional.empty();
        try {
            Part part = context.getRequest().getPart(fieldName);
            if (part != null) {
                result = Optional.of(this.temporaryAttachmentSessionsManager.uploadAttachment(documentReference, part));
            }
        } catch (IOException | ServletException e) {
            this.logger.warn("Error while reading the request content part: [{}]", getRootCauseMessage(e));
        } catch (TemporaryAttachmentException e) {
            this.logger.warn("Error while uploading the attachment: [{}]", getRootCauseMessage(e));
        }

        return result.map(attachment -> {
            Document document = Optional.ofNullable(attachment.getDoc())
                .map(doc -> doc.newDocument(context))
                .orElse(null);
            return new Attachment(document, attachment, context);
        }).orElse(null);
    }
}
