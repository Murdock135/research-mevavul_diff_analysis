class derbyConfig {
private DefaultJpaInstanceConfiguration derbyConfig() {
        DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
        File dbFolder = new File(tempFolder.getRoot(), "derbyDb");

        config.transactionType(DefaultJpaInstanceConfiguration.TransactionType.RESOURCE_LOCAL)
              .db(JpaDb.DERBY_EMBEDDED)
              .autoCreate(true)
              .url(dbFolder.getAbsolutePath())
              .user("test")
              .password("test")
              .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE)
              .
                      bind(JpaOptionDao.class, DefaultJpaOptionDao.class)
              .bind(JpaDao_LongInt.class, DefaultJpaDao_LongInt.class)
              .bind(JpaPatternDao.class, DefaultJpaPatternDao.class);

        return config;
    }
}
