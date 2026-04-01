class authenticate {
public Response authenticate(Document soapMessage) {
        try {
            return new PostBindingProtocol() {
                @Override
                protected String getBindingType(AuthnRequestType requestAbstractType) {
                    return SamlProtocol.SAML_SOAP_BINDING;
                }

                @Override
                protected boolean isDestinationRequired() {
                    return false;
                }

                @Override
                protected Response loginRequest(String relayState, AuthnRequestType requestAbstractType, ClientModel client) {
                    // Do not allow ECP login when client does not support it
                    if (!new SamlClient(client).allowECPFlow()) {
                        logger.errorf("Client %s is not allowed to execute ECP flow", client.getClientId());
                        throw new RuntimeException("Client is not allowed to use ECP profile.");
                    }

                    // force passive authentication when executing this profile
                    requestAbstractType.setIsPassive(true);
                    requestAbstractType.setDestination(session.getContext().getUri().getAbsolutePath());
                    return super.loginRequest(relayState, requestAbstractType, client);
                }
            }.execute(Soap.toSamlHttpPostMessage(soapMessage), null, null, null);
        } catch (Exception e) {
            String reason = "Some error occurred while processing the AuthnRequest.";
            String detail = e.getMessage();

            if (detail == null) {
                detail = reason;
            }

            return Soap.createFault().reason(reason).detail(detail).build();
        }
    }
}
