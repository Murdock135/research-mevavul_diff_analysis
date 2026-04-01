class initialize {
public Set<String> initialize(DocumentModelBridge document, DocumentDisplayerParameters parameters)
    {
        this.parameters = parameters;

        // Make sure the restricted property of the document is properly taken into account.
        if (document.isRestricted()) {
            parameters.setTransformationContextRestricted(true);
        }

        this.asyncProperties = this.asyncParser.getAsyncProperties(document);

        String transformationId = this.defaultEntityReferenceSerializer
            .serialize(parameters.isContentTransformed() && parameters.isTransformationContextIsolated()
                ? document.getDocumentReference() : this.documentAccessBridge.getCurrentDocumentReference());

        this.documentReference = document.getDocumentReference();

        if (this.asyncProperties.isAsyncAllowed() || this.asyncProperties.isCacheAllowed()) {
            this.id = createId("display", "document", "content",
                this.defaultEntityReferenceSerializer.serialize(this.documentReference), this.parameters.getSectionId(),
                this.parameters.getTargetSyntax() != null ? this.parameters.getTargetSyntax().toIdString() : "",
                transformationId, this.parameters.isContentTransformed(),
                this.parameters.isTransformationContextRestricted(), this.parameters.isTransformationContextIsolated());
        }

        this.executor.initialize(transformationId, document, parameters);

        return this.asyncProperties.getContextElements();
    }
}
