class prepareDocument {
protected XWikiDocument prepareDocument(HttpServletRequest request, EditForm editForm, ChangeRequest changeRequest)
        throws ChangeRequestException
    {
        XWikiContext context = this.contextProvider.get();
        String serializedDocReference = request.getParameter("docReference");
        DocumentReference documentReference = this.documentReferenceResolver.resolve(serializedDocReference);
        UserReference currentUserReference = this.currentUserReferenceResolver.resolve(CurrentUserReference.INSTANCE);
        if (!this.changeRequestRightsManager.isEditWithChangeRequestAllowed(currentUserReference, documentReference)) {
            throw new ChangeRequestException(
                String.format("User [%s] is not allowed to edit the document [%s] through a change request.",
                    currentUserReference, documentReference));
        }

        XWikiDocument modifiedDocument = null;
        try {
            if (isFromChangeRequest(request) && changeRequest != null) {
                List<FileChange> fileChangeList = this.fileChangeStorageManager.load(changeRequest, documentReference);
                String previousVersion = getPreviousVersion(request);
                FileChange fileChange = null;
                for (FileChange change : fileChangeList) {
                    if (change.getVersion().equals(previousVersion)) {
                        fileChange = change;
                        break;
                    }
                }
                if (fileChange != null) {
                    modifiedDocument = (XWikiDocument) fileChange.getModifiedDocument();
                } else {
                    throw new ChangeRequestException(
                        String.format("Cannot find file change with version [%s]", previousVersion));
                }
            } else {
                modifiedDocument = context.getWiki().getDocument(documentReference, context);
            }
            // cloning the document to ensure we don't impact the document in cache.
            modifiedDocument = modifiedDocument.clone();

            // Read info from the template if there's one.
            if (!StringUtils.isBlank(editForm.getTemplate())) {
                DocumentReference templateRef =
                    this.currentMixedDocumentReferenceResolver.resolve(editForm.getTemplate());

                // Check that the template can be read by current user.
                if (this.contextualAuthorizationManager.hasAccess(Right.VIEW, templateRef)) {
                    modifiedDocument.readFromTemplate(templateRef, context);
                }
            }

            modifiedDocument.readFromForm(editForm, context);
            if (modifiedDocument.getDefaultLocale() == Locale.ROOT) {
                modifiedDocument.setDefaultLocale(context.getWiki().getLocalePreference(context));
            }
            return modifiedDocument;
        } catch (XWikiException e) {
            throw new ChangeRequestException(
                String.format("Cannot read document [%s]", serializedDocReference), e);
        }
    }
}
