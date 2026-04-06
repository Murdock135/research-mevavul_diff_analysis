class createTenant {
public void createTenant(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("START - SETUP:CreateTenant: tenantKey: {}", tenant);

        try {
            tenantListRepository.addTenant(tenant);
            databaseService.create(tenant);
            databaseService.migrate(tenant);
            addUaaSpecification(tenant);
            addLoginsSpecification(tenant);
            addRoleSpecification(tenant);
            addPermissionSpecification(tenant);
            addDefaultEmailTemplates(tenant);
            log.info("STOP  - SETUP:CreateTenant: tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:CreateTenant: tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }
}
