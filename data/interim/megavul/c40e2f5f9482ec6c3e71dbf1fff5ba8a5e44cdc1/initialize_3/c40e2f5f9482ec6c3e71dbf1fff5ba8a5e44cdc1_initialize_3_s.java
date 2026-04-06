class initialize_3 {
@Override
    public void initialize() throws InitializationException
    {
        ListenerChain chain = new ListenerChain();
        setListenerChain(chain);

        // Construct the listener chain in the right order. Listeners early in the chain are called before listeners
        // placed later in the chain.
        chain.addListener(this);
        chain.addListener(new BlockStateChainingListener(chain));
        chain.addListener(new EmptyBlockChainingListener(chain));
        chain.addListener(new MetaDataStateChainingListener(chain));
        chain.addListener(new AnnotatedHTML5ChainingRenderer(this.linkRenderer, this.imageRenderer, chain));
    }
}
