class getConnection {
@Override
    public Connection getConnection(Context context) throws SQLException {
        DatabaseTypePojo type = databaseTypeDao.selectByDatabaseType(context.getDatabaseType());
        File driverFile = driverResources.loadOrDownload(context.getDatabaseType(), type.getJdbcDriverFileUrl());
        URLClassLoader loader = null;
        try {
            loader = new URLClassLoader(
                    new URL[]{
                            driverFile.toURI().toURL()
                    },
                    this.getClass().getClassLoader()
            );
        } catch (MalformedURLException e) {
            log.error("load driver error " + context, e);
            throw DomainErrors.CONNECT_DATABASE_FAILED.exception(e.getMessage());
        }
        // retrieve the driver class

        Class<?> clazz = null;
        Driver driver = null;
        try {
            clazz = Class.forName(type.getJdbcDriverClassName(), true, loader);
            driver = (Driver) clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            log.error("init driver error", e);
            throw DomainErrors.CONNECT_DATABASE_FAILED.exception("驱动初始化异常, 请检查 Driver name：" + e.getMessage());
        } catch (InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException
                 | NoSuchMethodException e) {
            log.error("init driver error", e);
            throw DomainErrors.CONNECT_DATABASE_FAILED.exception("驱动初始化异常：" + e.getMessage());
        }

        String urlPattern = type.getUrlPattern();
        String jdbcUrl = urlPattern.replace("{{jdbc.protocol}}", type.getJdbcProtocol())
                .replace("{{db.url}}", context.getUrl())
                .replace("{{db.name}}", context.getDatabaseName())
                .replace("{{db.schema}}", context.getSchemaName());
        Properties info = new Properties();
        info.put("user", context.getUsername());
        info.put("password", context.getPassword());
        return driver.connect(jdbcUrl, info);
    }
}
