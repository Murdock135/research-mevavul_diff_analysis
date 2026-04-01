class readObject {
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        DelegatingSerializationFilter filter = new DelegatingSerializationFilter();
        filter.setFilter(in, "org.keycloak.KeycloakSecurityContext;org.keycloak.KeycloakPrincipal;java.util.*;!*");
        in.defaultReadObject();
    }
}
