class getDatabaseUrl {
public static String getDatabaseUrl(DatabaseConfiguration dbConfig) {
        try {
            URI uri = new URI("jdbc:" + dbConfig.getDatabaseType().toLowerCase(), dbConfig.getDatabaseName(), null);
            return uri.toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
