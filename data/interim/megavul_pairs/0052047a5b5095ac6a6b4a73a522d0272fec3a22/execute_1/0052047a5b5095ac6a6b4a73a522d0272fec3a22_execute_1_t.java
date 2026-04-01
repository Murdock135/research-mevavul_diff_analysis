class execute_1 {
@Override
	public void execute(String jobToken, JobContext jobContext) {
		AgentQuery parsedQeury = AgentQuery.parse(agentQuery, true);
		TaskLogger jobLogger = jobContext.getLogger();
		OneDev.getInstance(ResourceManager.class).run(new AgentAwareRunnable() {

			@Override
			public void runOn(Long agentId, Session agentSession, AgentData agentData) {
				jobLogger.log(String.format("Executing job (executor: %s, agent: %s)...", getName(), agentData.getName()));
				jobContext.notifyJobRunning(agentId);

				List<Map<String, String>> registryLogins = new ArrayList<>();
				for (RegistryLogin login: getRegistryLogins()) {
					registryLogins.add(CollectionUtils.newHashMap(
							"url", login.getRegistryUrl(), 
							"userName", login.getUserName(), 
							"password", login.getPassword()));
				}
				
				List<Map<String, Serializable>> services = new ArrayList<>();
				for (Service service: jobContext.getServices())
					services.add(service.toMap());
				
				List<String> trustCertContent = getTrustCertContent();
				DockerJobData jobData = new DockerJobData(jobToken, getName(), jobContext.getProjectPath(), 
						jobContext.getProjectId(), jobContext.getCommitId().name(), jobContext.getBuildNumber(), 
						jobContext.getActions(), jobContext.getRetried(), services, registryLogins,
						mountDockerSock, trustCertContent, getRunOptions());
				
				try {
					WebsocketUtils.call(agentSession, jobData, 0);
				} catch (InterruptedException | TimeoutException e) {
					new Message(MessageType.CANCEL_JOB, jobToken).sendBy(agentSession);
				} 
				
			}
			
		}, new HashMap<>(), parsedQeury, jobContext.getResourceRequirements(), jobLogger);
		
	}
}
