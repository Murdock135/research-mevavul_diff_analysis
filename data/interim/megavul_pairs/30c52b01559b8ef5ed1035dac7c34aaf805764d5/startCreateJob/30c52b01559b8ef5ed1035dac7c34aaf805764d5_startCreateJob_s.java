class startCreateJob {
private Job startCreateJob(EntityReference entityReference, EditForm editForm) throws XWikiException
    {
        if (StringUtils.isBlank(editForm.getTemplate())) {
            // No template specified, nothing more to do.
            return null;
        }

        // If a template is set in the request, then this is a create action which needs to be handled by a create job,
        // but skipping the target document, which is now already saved by the save action.

        RefactoringScriptService refactoring =
            (RefactoringScriptService) Utils.getComponent(ScriptService.class, "refactoring");

        CreateRequest request = refactoring.getRequestFactory().createCreateRequest(Arrays.asList(entityReference));
        request.setCheckAuthorRights(false);
        // Set the target document.
        request.setEntityReferences(Arrays.asList(entityReference));
        // Set the template to use.
        DocumentReferenceResolver<String> resolver =
            Utils.getComponent(DocumentReferenceResolver.TYPE_STRING, "currentmixed");
        EntityReference templateReference = resolver.resolve(editForm.getTemplate());
        request.setTemplateReference(templateReference);
        // We`ve already created and populated the fields of the target document, focus only on the remaining children
        // specified in the template.
        request.setSkippedEntities(Arrays.asList(entityReference));

        Job createJob = refactoring.create(request);
        if (createJob != null) {
            return createJob;
        } else {
            throw new XWikiException(String.format("Failed to schedule the create job for [%s]", entityReference),
                refactoring.getLastError());
        }
    }
}
