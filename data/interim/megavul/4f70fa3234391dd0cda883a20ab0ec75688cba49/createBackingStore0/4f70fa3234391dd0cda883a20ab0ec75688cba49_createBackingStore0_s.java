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
        DataSource source;
        final InitialContext context;
        try {
            source = createDataSource();
            context = registerInto;
        } finally {
            registerInto = null;
        }
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
        /*
         * We now have a working connection. If a naming directory is running but didn't contains
         * the "jdbc/EPSG" entry, add it now. In such case, a message is prepared and logged.
         */
        LogRecord record;
        if (ALLOW_REGISTRATION && context != null) {
            try {
                context.bind(datasourceName, source);
                record =
                        Loggings.format(
                                Level.FINE,
                                LoggingKeys.CREATED_DATASOURCE_ENTRY_$1,
                                datasourceName);
            } catch (NamingException exception) {
                record =
                        Loggings.format(
                                Level.WARNING, LoggingKeys.CANT_BIND_DATASOURCE_$1, datasourceName);
                record.setThrown(exception);
            }
            log(record);
        }
        this.datasource = source; // Stores the data source only after success.
        return factory;
    }
}
