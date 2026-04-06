class checkHealth {
@PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @GetMapping(value = "/health")
  public Health checkHealth(@RequestParam String instanceId) {
    List<Env> allEnvs = portalSettings.getAllEnvs();

    ServiceDTO service = null;
    for (final Env env : allEnvs) {
      EnvironmentInfo envInfo = adaptEnv2EnvironmentInfo(env);
      for (final ServiceDTO s : envInfo.getAdminServices()) {
        if (instanceId.equals(s.getInstanceId())) {
          service = s;
          break;
        }
      }
      for (final ServiceDTO s : envInfo.getConfigServices()) {
        if (instanceId.equals(s.getInstanceId())) {
          service = s;
          break;
        }
      }
    }

    if (service == null) {
      throw new IllegalArgumentException("No such instance of instanceId: " + instanceId);
    }

    return restTemplate.getForObject(service.getHomepageUrl() + "/health", Health.class);
  }
}
