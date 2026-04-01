class initDbCommands {
private void initDbCommands(Environment env) {
        String schemaSuffix = env.getProperty(DB_SCHEMA_SUFFIX, EMPTY);

        DB_COMMANDS.put("POSTGRESQL", "SET search_path TO %s" + schemaSuffix);
        DB_COMMANDS.put("ORACLE", "ALTER SESSION SET CURRENT_SCHEMA = %s" + schemaSuffix);
        DB_COMMANDS.put("H2", DEFAULT_COMMAND + schemaSuffix);
    }
}
