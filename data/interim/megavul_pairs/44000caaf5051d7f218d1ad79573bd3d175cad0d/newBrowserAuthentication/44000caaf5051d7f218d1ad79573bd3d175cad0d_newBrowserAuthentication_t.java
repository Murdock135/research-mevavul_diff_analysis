class newBrowserAuthentication {
@Override
    protected Response newBrowserAuthentication(AuthenticationSessionModel authSession, boolean isPassive, boolean redirectToAuthentication, SamlProtocol samlProtocol) {
        // Saml ECP flow creates only TRANSIENT user sessions
        authSession.setClientNote(AuthenticationManager.USER_SESSION_PERSISTENT_STATE, UserSessionModel.SessionPersistenceState.TRANSIENT.toString());
        return super.newBrowserAuthentication(authSession, isPassive, redirectToAuthentication, createEcpSamlProtocol());
    }
}
