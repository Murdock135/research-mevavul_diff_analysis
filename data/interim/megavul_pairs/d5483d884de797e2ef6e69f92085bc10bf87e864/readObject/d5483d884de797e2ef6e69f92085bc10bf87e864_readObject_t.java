class readObject {
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        DelegatingSerializationFilter filter = new DelegatingSerializationFilter();
        filter.setFilter(in, "org.keycloak.KeycloakSecurityContext;!*");
        in.defaultReadObject();

        token = parseToken(tokenString, AccessToken.class);
        idToken = parseToken(idTokenString, IDToken.class);
    }
}
