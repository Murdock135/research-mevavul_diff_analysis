class createBackingStore0 {
private AbstractAuthorityFactory createBackingStore0() throws FactoryException, SQLException {
        /*
         * We are locking on ReferencingFactoryFinder to avoid deadlocks.
         * @see DeferredAuthorityFactory#getBackingStore()
         */
        assert Thread.holdsLock(ReferencingFactoryFinder.class);
        final Hints sourceHints = new Hints(hints);
        sourceHints.putAll(factories.getImplementationHints());
        if (datasource != null) {
            return createBackingStore(sourceHints);
        }
        /*
         * Try to gets the DataSource from JNDI. In case of success, it will be tried
         * for a connection before any DataSource declared in META-INF/services/.
         */
        DataSource source = createDataSource();
        if (source == null) {
            throw new FactoryNotFoundException(Errors.format(ErrorKeys.NO_DATA_SOURCE));
        }
        final AbstractAuthorityFactory factory;
        try {
            datasource = source;
            factory = createBackingStore(sourceHints);
        } finally {
            datasource = null;
        }
        this.datasource = source; // Stores the data source only after success.
        return factory;
    }
}
