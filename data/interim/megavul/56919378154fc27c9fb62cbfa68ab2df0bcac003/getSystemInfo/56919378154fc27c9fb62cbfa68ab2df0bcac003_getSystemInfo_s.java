class getSystemInfo {
@PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @GetMapping
  public SystemInfo getSystemInfo() {
    SystemInfo systemInfo = new SystemInfo();

    String version = Apollo.VERSION;
    if (isValidVersion(version)) {
      systemInfo.setVersion(version);
    }

    List<Env> allEnvList = portalSettings.getAllEnvs();

    for (Env env : allEnvList) {
      EnvironmentInfo environmentInfo = new EnvironmentInfo();
      String metaServerAddresses = MetaDomainConsts.getMetaServerAddress(env);

      environmentInfo.setEnv(env);
      environmentInfo.setActive(portalSettings.isEnvActive(env));
      environmentInfo.setMetaServerAddress(metaServerAddresses);

      String selectedMetaServerAddress = MetaDomainConsts.getDomain(env);
      try {
        environmentInfo.setConfigServices(getServerAddress(selectedMetaServerAddress, CONFIG_SERVICE_URL_PATH));

        environmentInfo.setAdminServices(getServerAddress(selectedMetaServerAddress, ADMIN_SERVICE_URL_PATH));
      } catch (Throwable ex) {
        String errorMessage = "Loading config/admin services from meta server: " + selectedMetaServerAddress + " failed!";
        logger.error(errorMessage, ex);
        environmentInfo.setErrorMessage(errorMessage + " Exception: " + ex.getMessage());
      }

      systemInfo.addEnvironment(environmentInfo);
    }

    return systemInfo;
  }
}
