class lookupDataSource {
static DataSource lookupDataSource(Hints hints) throws FactoryException {
        Object hint = hints.get(Hints.EPSG_DATA_SOURCE);
        if (hint instanceof DataSource) {
            return (DataSource) hint;
        } else if (hint instanceof String) {
            String name = (String) hint;
            try {
                return (DataSource) GeoTools.jndiLookup(name);
            } catch (Exception e) {
                throw new FactoryException("EPSG_DATA_SOURCE '" + name + "' not found:" + e, e);
            }
        }
        throw new FactoryException("EPSG_DATA_SOURCE must be provided");
    }
}
