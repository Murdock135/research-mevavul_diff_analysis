class createDataSource_1 {
protected DataSource createDataSource() throws SQLException {
        InitialContext context = null;
        DataSource source = null;
        try {
            context = GeoTools.getInitialContext();
            source = (DataSource) context.lookup(datasourceName);
        } catch (IllegalArgumentException | NoInitialContextException exception) {
            // Fall back on 'return null' below.
        } catch (NamingException exception) {
            registerInto = context;
            // Fall back on 'return null' below.
        }
        return source;
    }
}
