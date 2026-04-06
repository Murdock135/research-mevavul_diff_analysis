class newBrowserAuthentication {
@Override
    protected Response newBrowserAuthentication(AuthenticationSessionModel authSession, boolean isPassive, boolean redirectToAuthentication, SamlProtocol samlProtocol) {
        return super.newBrowserAuthentication(authSession, isPassive, redirectToAuthentication, createEcpSamlProtocol());
    }
}
