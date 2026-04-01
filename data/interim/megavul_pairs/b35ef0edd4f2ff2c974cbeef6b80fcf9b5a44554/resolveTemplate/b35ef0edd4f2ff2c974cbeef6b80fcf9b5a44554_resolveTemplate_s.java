class resolveTemplate {
protected DocumentReference resolveTemplate(String template)
    {
        if (StringUtils.isNotBlank(template)) {
            DocumentReference templateReference = this.currentmixedReferenceResolver.resolve(template);

            // Make sure the current user have access to the template document before copying it
            if (this.autorization.hasAccess(Right.VIEW, templateReference)) {
                return templateReference;
            }
        }

        return null;
    }
}
