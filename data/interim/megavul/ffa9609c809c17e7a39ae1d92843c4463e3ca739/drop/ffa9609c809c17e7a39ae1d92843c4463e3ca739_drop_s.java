class drop {
@SneakyThrows
    public void drop(String tenantKey) {
        StopWatch stopWatch = createStarted();
        log.info("START - SETUP:DeleteTenant:schema tenantKey: {}", tenantKey);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format(schemaDropResolver.getSchemaDropCommand(), tenantKey));
            log.info("STOP  - SETUP:DeleteTenant:schema tenantKey: {}, result: OK, time = {} ms",
                tenantKey, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:DeleteTenant:schema tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }
}
