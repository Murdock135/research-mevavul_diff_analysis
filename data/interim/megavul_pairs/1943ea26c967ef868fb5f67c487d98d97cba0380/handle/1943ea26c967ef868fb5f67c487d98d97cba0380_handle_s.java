class handle {
@Override
    public void handle(ResourceReference reference, ResourceReferenceHandlerChain chain)
        throws ResourceReferenceHandlerException
    {
        AuthenticationResourceReference authenticationResourceReference = (AuthenticationResourceReference) reference;

        switch (authenticationResourceReference.getAction()) {
            case RETRIEVE_USERNAME:
                this.handleAction("forgotusername", authenticationResourceReference.getWikiReference());
                break;

            case RESET_PASSWORD:
                this.handleAction("resetpassword", authenticationResourceReference.getWikiReference());
                break;

            default:
                // nothing to do here.
        }

        // Be a good citizen, continue the chain, in case some lower-priority Handler has something to do for this
        // Resource Reference.
        chain.handleNext(reference);
    }
}
