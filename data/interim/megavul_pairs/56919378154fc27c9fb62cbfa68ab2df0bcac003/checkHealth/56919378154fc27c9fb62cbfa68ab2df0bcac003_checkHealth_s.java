class checkHealth {
@PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @GetMapping(value = "/health")
  public Health checkHealth(@RequestParam String host) {
    return restTemplate.getForObject(host + "/health", Health.class);
  }
}
