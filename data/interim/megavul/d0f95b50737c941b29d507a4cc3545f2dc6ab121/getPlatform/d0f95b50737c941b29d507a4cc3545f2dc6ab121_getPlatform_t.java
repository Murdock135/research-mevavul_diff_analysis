class getPlatform {
public Platform getPlatform(String platformKey, String workspaceId) {
        IntegrationRequest integrationRequest = new IntegrationRequest();
        integrationRequest.setPlatform(platformKey);
        integrationRequest.setWorkspaceId(StringUtils.isBlank(workspaceId) ? SessionUtils.getCurrentWorkspaceId() : workspaceId);
        ServiceIntegration serviceIntegration = baseIntegrationService.get(integrationRequest);

        PlatformRequest pluginRequest = new PlatformRequest();
        pluginRequest.setWorkspaceId(workspaceId);
        pluginRequest.setIntegrationConfig(serviceIntegration.getConfiguration());
        Platform platform = getPluginManager().getPlatformByKey(platformKey, pluginRequest);
        if (platform == null) {
            MSException.throwException(Translator.get("platform_plugin_not_exit") + PLUGIN_DOWNLOAD_URL);
        }
        return platform;
    }
}
