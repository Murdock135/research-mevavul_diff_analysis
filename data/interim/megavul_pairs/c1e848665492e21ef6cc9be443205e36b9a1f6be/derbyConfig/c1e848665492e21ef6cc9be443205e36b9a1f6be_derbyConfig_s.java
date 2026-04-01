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
                      bind(OptionJpaDao_LongInt.class, DefaultOptionJpaDao_LongInt.class)
              .bind(JpaDao_LongInt.class, DefaultJpaDao_LongInt.class)
              .bind(JpaPatternDao.class, DefaultJpaPatternDao_LongInt.class);

        return config;
    }
}
