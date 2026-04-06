class prepareEditedDocument {
protected XWikiDocument prepareEditedDocument(XWikiContext context) throws XWikiException
    {
        // Determine the edited document (translation).
        XWikiDocument editedDocument = getEditedDocument(context);
        EditForm editForm = (EditForm) context.getForm();

        // Update the edited document based on the template specified on the request.
        readFromTemplate(editedDocument, editForm.getTemplate(), context);

        // The default values from the template can be overwritten by additional request parameters.
        updateDocumentTitleAndContentFromRequest(editedDocument, context);
        editedDocument.readAddedUpdatedAndRemovedObjectsFromForm(editForm, context);

        // Set the current user as creator, author and contentAuthor when the edited document is newly created to avoid
        // using XWikiGuest instead (because those fields were not previously initialized).
        if (editedDocument.isNew()) {
            editedDocument.setCreatorReference(context.getUserReference());
            editedDocument.setAuthorReference(context.getUserReference());
            editedDocument.setContentAuthorReference(context.getUserReference());
        }
        editedDocument.readTemporaryUploadedFiles(editForm);

        // Expose the edited document on the XWiki context and the Velocity context.
        putDocumentOnContext(editedDocument, context);

        return editedDocument;
    }
}
