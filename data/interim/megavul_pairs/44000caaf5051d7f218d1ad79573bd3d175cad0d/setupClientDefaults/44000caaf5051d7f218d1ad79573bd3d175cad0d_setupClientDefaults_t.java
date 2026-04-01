class setupClientDefaults {
@Override
    public void setupClientDefaults(ClientRepresentation clientRep, ClientModel newClient) {
        SamlRepresentationAttributes rep = new SamlRepresentationAttributes(clientRep.getAttributes());
        SamlClient client = new SamlClient(newClient);
        if (clientRep.isStandardFlowEnabled() == null) newClient.setStandardFlowEnabled(true);
        if (rep.getCanonicalizationMethod() == null) {
            client.setCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE);
        }
        if (rep.getSignatureAlgorithm() == null) {
            client.setSignatureAlgorithm(SignatureAlgorithm.RSA_SHA256);
        }

        if (rep.getNameIDFormat() == null) {
            client.setNameIDFormat("username");
        }

        if (rep.getIncludeAuthnStatement() == null) {
            client.setIncludeAuthnStatement(true);
        }

        if (rep.getForceNameIDFormat() == null) {
            client.setForceNameIDFormat(false);
        }

        if (rep.getAllowEcpFlow() == null) {
            client.setAllowECPFlow(false);
        }

        if (rep.getSamlServerSignature() == null) {
            client.setRequiresRealmSignature(true);
        }
        if (rep.getForcePostBinding() == null) {
            client.setForcePostBinding(true);
        }

        if (rep.getClientSignature() == null) {
            client.setRequiresClientSignature(true);
        }

        if (client.requiresClientSignature() && client.getClientSigningCertificate() == null) {
            CertificateRepresentation info = KeycloakModelUtils.generateKeyPairCertificate(newClient.getClientId());
            client.setClientSigningCertificate(info.getCertificate());
            client.setClientSigningPrivateKey(info.getPrivateKey());

        }

        if (clientRep.isFrontchannelLogout() == null) {
            newClient.setFrontchannelLogout(true);
        }

        client.setArtifactBindingIdentifierFrom(clientRep.getClientId());
    }
}
