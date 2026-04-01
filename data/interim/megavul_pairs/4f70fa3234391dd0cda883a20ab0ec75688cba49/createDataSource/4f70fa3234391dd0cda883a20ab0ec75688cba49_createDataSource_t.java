class createDataSource {
@Override
    protected DataSource createDataSource(Map<String, ?> params, SQLDialect dialect)
            throws IOException {
        String jndiName = (String) JNDI_REFNAME.lookUp(params);
        if (jndiName == null) throw new IOException("Missing " + JNDI_REFNAME.description);

        DataSource ds = null;

        try {
            ds = (DataSource) GeoTools.jndiLookup(jndiName);
        } catch (NamingException e1) {
            // check if the user did not specify "java:comp/env"
            // and this code is running in a J2EE environment
            try {
                if (jndiName.startsWith(J2EERootContext) == false) {
                    ds = (DataSource) GeoTools.jndiLookup(J2EERootContext + jndiName);
                    // success --> issue a waring
                    Logger.getLogger(this.getClass().getName())
                            .log(
                                    Level.WARNING,
                                    "Using "
                                            + J2EERootContext
                                            + jndiName
                                            + " instead of "
                                            + jndiName
                                            + " would avoid an unnecessary JNDI lookup");
                }
            } catch (NamingException e2) {
                // do nothing, was only a try
            }
        }

        if (ds == null) throw new IOException("Cannot find JNDI data source: " + jndiName);
        else return ds;
    }
}
