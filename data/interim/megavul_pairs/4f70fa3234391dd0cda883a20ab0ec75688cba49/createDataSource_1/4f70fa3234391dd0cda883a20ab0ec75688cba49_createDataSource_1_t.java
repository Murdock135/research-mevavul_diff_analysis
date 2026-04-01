class createDataSource_1 {
protected DataSource createDataSource() throws SQLException {
        DataSource source = null;
        try {
            source = (DataSource) GeoTools.jndiLookup(datasourceName);
        } catch (IllegalArgumentException | NamingException exception) {
            // Fall back on 'return null' below.
        }
        return source;
    }
}
