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

        // If TLS is required but the server doesn't offer it, disconnect
        // from the server and throw an error. First check if we've already negotiated TLS
        // and are secure, however (features get parsed a second time after TLS is established).
        if (!isSecureConnection() && getConfiguration().getSecurityMode() == SecurityMode.required) {
            throw new SecurityRequiredByClientException();
        }

        // Make note of the fact that we're now connected.
        connected = true;
        callConnectionConnectedListener();

        return this;
    }
}
