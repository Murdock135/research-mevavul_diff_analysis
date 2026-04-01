class getSyntaxRenderer_2 {
@Override
    protected AbstractChainingPrintRenderer getSyntaxRenderer()
    {
        return new HTML5ChainingRenderer(this.linkRenderer, this.imageRenderer, this.htmlElementSanitizer,
            getListenerChain());
    }
}
