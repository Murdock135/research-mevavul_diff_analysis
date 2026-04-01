class resolveTemplate {
protected DocumentReference resolveTemplate(String template)
    {
        if (StringUtils.isNotBlank(template)) {
            DocumentReference templateReference = getCurrentMixedDocumentReferenceResolver().resolve(template);

            // Make sure the current user have access to the template document before copying it
            if (getContextualAuthorizationManager().hasAccess(Right.VIEW, templateReference)) {
                return templateReference;
            }
        }

        return null;
    }
}
