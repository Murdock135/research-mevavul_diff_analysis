class connect {
public synchronized AbstractXMPPConnection connect() throws SmackException, IOException, XMPPException, InterruptedException {
        // Check if not already connected
        throwAlreadyConnectedExceptionIfAppropriate();

        // Reset the connection state
        saslAuthentication.init();
        saslFeatureReceived.init();
        lastFeaturesReceived.init();
        streamId = null;

        // Perform the actual connection to the XMPP service
        connectInternal();

        // Wait with SASL auth until the SASL mechanisms have been received
        saslFeatureReceived.checkIfSuccessOrWaitOrThrow();

        // Make note of the fact that we're now connected.
        connected = true;
        callConnectionConnectedListener();

        return this;
    }
}
