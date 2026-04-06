class checkRegistrationAuthorization {
private void checkRegistrationAuthorization(XWikiDocument document, Scope scope) throws AccessDeniedException
    {
        switch (scope) {
            case GLOBAL:
                this.authorizationManager.checkAccess(Right.PROGRAM, document.getAuthorReference(), null);
                break;
            case WIKI:
                this.authorizationManager.checkAccess(Right.ADMIN, document.getAuthorReference(), document
                    .getDocumentReference().getWikiReference());
                break;
            case USER:
                if (this.configuration.isRestrictUserTranslations()) {
                    this.authorizationManager.checkAccess(Right.SCRIPT, document.getAuthorReference(),
                        document.getDocumentReference());
                }
                break;
            default:
                break;
        }
    }
}
