class simpleAuthenticationGenerator {
private void simpleAuthenticationGenerator(XmlGenerator gen, SimpleAuthenticationConfig c) {
        if (c == null) {
            return;
        }
        XmlGenerator simpleGen = gen.open("simple");
        addClusterLoginElements(simpleGen, c).nodeIfContents("role-separator", c.getRoleSeparator());
        for (String username : c.getUsernames()) {
            simpleGen.open("user", "username", username, "password", getOrMaskValue(c.getPassword(username)));
            for (String role : c.getRoles(username)) {
                simpleGen.node("role", role);
            }
            // close <user> node
            simpleGen.close();
        }
        simpleGen.close();
    }
}
