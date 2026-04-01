class getDatabaseUrl {
public static String getDatabaseUrl(DatabaseConfiguration dbConfig) {
        return "jdbc:" + dbConfig.getDatabaseType().toLowerCase() + ":" + dbConfig.getDatabaseName();
    }
}
