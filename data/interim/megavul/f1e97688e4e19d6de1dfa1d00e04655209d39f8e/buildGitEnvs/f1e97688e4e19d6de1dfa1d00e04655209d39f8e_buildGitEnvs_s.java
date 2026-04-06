class buildGitEnvs {
private Map<String, String> buildGitEnvs(Project project) {
		Map<String, String> environments = new HashMap<>();
		
		ServerConfig serverConfig = OneDev.getInstance(ServerConfig.class);
		String serverUrl;
        if (serverConfig.getHttpPort() != 0)
            serverUrl = "http://localhost:" + serverConfig.getHttpPort();
        else 
            serverUrl = "https://localhost:" + serverConfig.getHttpsPort();

        SettingManager settingManager = OneDev.getInstance(SettingManager.class);
        environments.put("ONEDEV_CURL", settingManager.getSystemSetting().getCurlConfig().getExecutable());
		environments.put("ONEDEV_URL", serverUrl);
		environments.put("ONEDEV_USER_ID", SecurityUtils.getUserId().toString());
		environments.put("ONEDEV_REPOSITORY_ID", project.getId().toString());
		return environments;
    }
}
