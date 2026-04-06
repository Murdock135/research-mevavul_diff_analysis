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
      EnvironmentInfo environmentInfo = adaptEnv2EnvironmentInfo(env);

      systemInfo.addEnvironment(environmentInfo);
    }

    return systemInfo;
  }
}
