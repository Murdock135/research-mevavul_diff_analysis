class getSyntaxRenderer_3 {
@Override
    protected AbstractChainingPrintRenderer getSyntaxRenderer()
    {
        return new AnnotatedXHTMLChainingRenderer(this.linkRenderer, this.imageRenderer, this.htmlElementSanitizer,
            getListenerChain());
    }
}
