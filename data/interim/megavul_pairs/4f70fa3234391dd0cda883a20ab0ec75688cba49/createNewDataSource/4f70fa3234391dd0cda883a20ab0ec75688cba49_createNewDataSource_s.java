class createNewDataSource {
@Override
    public DataSource createNewDataSource(Map<String, ?> params) throws IOException {
        String refName = (String) JNDI_REFNAME.lookUp(params);
        try {
            return (DataSource) GeoTools.getInitialContext().lookup(refName);
        } catch (Exception e) {
            throw new DataSourceException("Could not find the specified data source in JNDI", e);
        }
    }
}
