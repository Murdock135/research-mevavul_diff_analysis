class initDbCommands {
private void initDbCommands(Environment env) {
        String schemaSuffix = env.getProperty(DB_SCHEMA_SUFFIX, EMPTY);

        DB_COMMANDS.put("POSTGRESQL", "SET search_path TO %s" + schemaSuffix);
        DB_COMMANDS.put("ORACLE", "ALTER SESSION SET CURRENT_SCHEMA = %s" + schemaSuffix);
        DB_COMMANDS.put("H2", DEFAULT_COMMAND + schemaSuffix);

        DB_SQL_COMMANDS.put("POSTGRESQL", "SET search_path TO ?" + schemaSuffix);
        DB_SQL_COMMANDS.put("ORACLE", "ALTER SESSION SET CURRENT_SCHEMA = ?" + schemaSuffix);
        DB_SQL_COMMANDS.put("H2", DEFAULT_COMMAND_SQL_COMMAND + schemaSuffix);
    }
}
