class getSyntaxRenderer_1 {
@Override
    protected AbstractChainingPrintRenderer getSyntaxRenderer()
    {
        return new AnnotatedHTML5ChainingRenderer(this.linkRenderer, this.imageRenderer, getListenerChain());
    }
}
