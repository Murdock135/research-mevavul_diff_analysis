class executeContext {
private List<Block> executeContext(XDOM xdom, ContextMacroParameters parameters, MacroTransformationContext context)
        throws MacroExecutionException
    {
        DocumentReference referencedDocReference;
        if (parameters.getDocument() != null) {
            referencedDocReference =
                this.macroReferenceResolver.resolve(parameters.getDocument(), context.getCurrentMacroBlock());
            DocumentReference currentAuthor = this.documentAccessBridge.getCurrentAuthorReference();

            // Make sure the author is allowed to use the target document
            checkAccess(currentAuthor, referencedDocReference);
        } else {
            referencedDocReference = null;
        }

        // Reuse the very generic async rendering framework (even if we don't do async and caching) since it's
        // taking
        // care of many other things
        BlockAsyncRendererConfiguration configuration = createBlockAsyncRendererConfiguration(null, xdom, context);
        configuration.setAsyncAllowed(false);
        configuration.setCacheAllowed(false);

        if (parameters.isRestricted()) {
            configuration.setResricted(true);
        }

        Map<String, Object> backupObjects = null;
        try {
            if (referencedDocReference != null) {
                backupObjects = new HashMap<>();

                // Switch the context document
                this.documentAccessBridge.pushDocumentInContext(backupObjects, referencedDocReference);

                // Apply the transformations but with a Transformation Context having the XDOM of the passed
                // document so that macros execute on the passed document's XDOM (e.g. the TOC macro will generate
                // the toc for the passed document instead of the current document).
                DocumentModelBridge referencedDoc =
                    this.documentAccessBridge.getTranslatedDocumentInstance(referencedDocReference);
                XDOM referencedXDOM = referencedDoc.getXDOM();

                if (parameters.getTransformationContext() == TransformationContextMode.TRANSFORMATIONS) {
                    // Get the XDOM from the referenced doc but with Transformations applied so that all macro are
                    // executed and contribute XDOM elements.
                    // IMPORTANT: This can be dangerous since it means executing macros, and thus also script macros
                    // defined in the referenced document. To be used with caution.
                    TransformationContext referencedTxContext =
                        new TransformationContext(referencedXDOM, referencedDoc.getSyntax(),
                            referencedDoc.isRestricted());
                    this.transformationManager.performTransformations(referencedXDOM, referencedTxContext);
                }

                // Configure the Transformation Context XDOM depending on the mode asked.
                configuration.setXDOM(referencedXDOM);
            }

            // Execute the content
            Block result = this.executor.execute(configuration);

            return result.getChildren();
        } catch (Exception e) {
            throw new MacroExecutionException("Failed start the execution of the macro", e);
        } finally {
            if (backupObjects != null) {
                // Restore the context document
                this.documentAccessBridge.popDocumentFromContext(backupObjects);
            }
        }
    }
}
