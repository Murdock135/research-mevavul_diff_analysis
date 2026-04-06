class resetPassword {
@Override
    public void resetPassword(UserReference userReference, String newPassword)
        throws ResetPasswordException
    {
        if (this.checkUserReference(userReference)) {
            XWikiContext context = this.contextProvider.get();

            DocumentUserReference documentUserReference = (DocumentUserReference) userReference;
            DocumentReference reference = documentUserReference.getReference();

            try {
                XWikiDocument userDocument = context.getWiki().getDocument(reference, context);
                userDocument.removeXObjects(RESET_PASSWORD_REQUEST_CLASS_REFERENCE);
                BaseObject userXObject = userDocument.getXObject(USER_CLASS_REFERENCE);

                // /!\ We cannot use BaseCollection#setStringValue as it's storing value in plain text.
                userXObject.set("password", newPassword, context);

                String saveComment = this.localizationManager.getTranslationPlain(
                    "xe.admin.passwordReset.step2.versionComment.passwordReset");
                context.getWiki().saveDocument(userDocument, saveComment, true, context);
            } catch (XWikiException e) {
                throw new ResetPasswordException("Cannot open user document to perform reset password.", e);
            }
        }
    }
}
