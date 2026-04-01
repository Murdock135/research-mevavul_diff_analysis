class migrate {
@SneakyThrows
    public void migrate(String tenantKey) {
        final StopWatch stopWatch = createStarted();
        try {
            log.info("START - SETUP:CreateTenant:liquibase tenantKey: {}", tenantKey);
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setResourceLoader(resourceLoader);
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog(CHANGE_LOG_PATH);
            liquibase.setContexts(liquibaseProperties.getContexts());
            liquibase.setDefaultSchema(tenantKey);
            liquibase.setDropFirst(liquibaseProperties.isDropFirst());
            liquibase.setChangeLogParameters(DatabaseUtil.defaultParams(tenantKey));
            liquibase.setShouldRun(true);
            liquibase.afterPropertiesSet();
            log.info("STOP  - SETUP:CreateTenant:liquibase tenantKey: {}, result: OK, time = {} ms", tenantKey,
                stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:CreateTenant:liquibase tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }
}
