class createSchema {
public static void createSchema(DataSource dataSource, String name) {
        assertTenantKeyValid(name);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format(DDL_CREATE_SCHEMA, name));
        } catch (SQLException e) {
            throw new RuntimeException("Can not connect to database", e);
        }
    }
}
