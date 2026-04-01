class handle {
@Override
    public void handle(ResourceReference reference, ResourceReferenceHandlerChain chain)
        throws ResourceReferenceHandlerException
    {
        AuthenticationResourceReference authenticationResourceReference = (AuthenticationResourceReference) reference;

        WikiReference wikiReference = authenticationResourceReference.getWikiReference();
        try {
            if (!this.wikiDescriptorManager.exists(wikiReference.getName())) {
                throw new ResourceReferenceHandlerException(
                    String.format("The wiki [%s] does not exist.", wikiReference.getName()));
            }
        } catch (WikiManagerException e) {
            throw new ResourceReferenceHandlerException(
                String.format("Error when checking if wiki [%s] exists.", wikiReference.getName()), e);
        }

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
