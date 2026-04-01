class uploadTemporaryAttachment {
public XWikiAttachment uploadTemporaryAttachment(DocumentReference documentReference, String fieldName)
    {
        XWikiContext context = this.contextProvider.get();
        XWikiAttachment result = null;
        try {
            Part part = context.getRequest().getPart(fieldName);
            if (part != null) {
                result = this.temporaryAttachmentSessionsManager.uploadAttachment(documentReference, part);
            }
        } catch (IOException | ServletException e) {
            logger.warn("Error while reading the request content part: [{}]", ExceptionUtils.getRootCauseMessage(e));
        } catch (TemporaryAttachmentException e) {
            logger.warn("Error while uploading the attachment: [{}]", ExceptionUtils.getRootCauseMessage(e));
        }
        return result;
    }
}
