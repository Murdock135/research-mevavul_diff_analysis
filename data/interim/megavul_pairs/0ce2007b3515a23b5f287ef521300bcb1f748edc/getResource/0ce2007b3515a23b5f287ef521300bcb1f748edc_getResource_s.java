class getResource {
private BufferedReader getResource(final String name) {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(name))
        );
    }
}
