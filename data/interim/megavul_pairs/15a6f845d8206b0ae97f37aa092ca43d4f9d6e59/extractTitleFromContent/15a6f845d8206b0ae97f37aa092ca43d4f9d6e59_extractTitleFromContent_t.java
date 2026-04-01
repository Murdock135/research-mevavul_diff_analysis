class extractTitleFromContent {
@Override
    protected XDOM extractTitleFromContent(DocumentModelBridge document, DocumentDisplayerParameters parameters)
    {
        // Note: Ideally we should apply transformations on the document's returned XDOM here since macros could
        // generate headings for example or some other transformations could modify headings. However we don't do this
        // at the moment since it would be too costly to do so. In the future we will even probably remove the feature
        // of generating the title from the content.
        List<HeaderBlock> blocks =
            document.getXDOM().getBlocks(new ClassBlockMatcher(HeaderBlock.class), Block.Axes.DESCENDANT);
        if (!blocks.isEmpty()) {
            HeaderBlock heading = blocks.get(0);
            // Check the heading depth after which we should return null if no heading was found.
            if (heading.getLevel().getAsInt() <= displayConfiguration.getTitleHeadingDepth()) {
                XDOM headingXDOM = new XDOM(Collections.<Block> singletonList(heading));
                try {
                    TransformationContext txContext =
                        new TransformationContext(headingXDOM, document.getSyntax(),
                            parameters.isTransformationContextRestricted() || document.isRestricted());
                    txContext.setTargetSyntax(parameters.getTargetSyntax());
                    transformationManager.performTransformations(headingXDOM, txContext);

                    Block headingBlock = headingXDOM.getChildren().size() > 0 ? headingXDOM.getChildren().get(0) : null;
                    if (headingBlock instanceof HeaderBlock) {
                        return new XDOM(headingBlock.getChildren());
                    }
                } catch (TransformationException e) {
                    getLogger().warn("Failed to extract title from document content.");
                }
            }
        }
        return null;
    }
}
