class getSyntaxRenderer {
@Override
    protected AbstractChainingPrintRenderer getSyntaxRenderer()
    {
        return new XHTMLChainingRenderer(this.linkRenderer, this.imageRenderer, getListenerChain());
    }
}
