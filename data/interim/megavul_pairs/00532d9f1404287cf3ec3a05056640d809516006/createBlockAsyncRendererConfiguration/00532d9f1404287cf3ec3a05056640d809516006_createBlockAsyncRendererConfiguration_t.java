class createBlockAsyncRendererConfiguration {
protected BlockAsyncRendererConfiguration createBlockAsyncRendererConfiguration(List<?> idElements, Block content,
        String source, MacroTransformationContext context)
    {
        BlockAsyncRendererConfiguration configuration = new BlockAsyncRendererConfiguration(idElements, content);

        // Set author
        if (source != null) {
            DocumentReference sourceReference = this.resolver.resolve(source);
            configuration.setSecureReference(sourceReference, this.documentAccessBridge.getCurrentAuthorReference());

            // Invalidate the cache when the document containing the macro call is modified
            configuration.useEntity(sourceReference);
        }

        // Indicate if the result should be inline or not
        configuration.setInline(context.isInline());

        // Indicate the syntax of the content
        configuration.setDefaultSyntax(this.parser.getCurrentSyntax(context));

        // Indicate the target syntax
        configuration.setTargetSyntax(this.renderingContext.getTargetSyntax());

        // Set the transformation id
        configuration.setTransformationId(context.getTransformationContext().getId());

        // Indicate if we are in a restricted mode
        configuration.setResricted(context.getTransformationContext().isRestricted());

        return configuration;
    }
}
