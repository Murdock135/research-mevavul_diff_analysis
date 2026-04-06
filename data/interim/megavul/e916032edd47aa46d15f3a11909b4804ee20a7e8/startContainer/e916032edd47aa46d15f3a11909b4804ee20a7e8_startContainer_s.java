class startContainer {
public void startContainer(String containerId) {

        KieServerState currentState = context.getStateRepository().load(KieServerEnvironment.getServerId());
        Set<String> controllers = currentState.getControllers();

        KieServerConfig config = currentState.getConfiguration();
        if (controllers != null && !controllers.isEmpty()) {
            for (String controllerUrl : controllers) {

                if (controllerUrl != null && !controllerUrl.isEmpty()) {
                    String connectAndSyncUrl = controllerUrl + "/management/servers/" + KieServerEnvironment.getServerId() + "/containers/" + containerId + "/status/started";

                    String userName = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_USER, "kieserver");
                    String password = loadPassword(config);
                    String token = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_TOKEN);

                    try {
                        makeHttpPostRequestAndCreateCustomResponse(connectAndSyncUrl, "", null, userName, password, token);
                        break;
                    } catch (Exception e) {
                        // let's check all other controllers in case of running in cluster of controllers
                        logger.warn("Exception encountered while syncing with controller at {} error {}", connectAndSyncUrl, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
                        logger.debug("Exception encountered while syncing with controller at {} error {}", connectAndSyncUrl, e.getMessage(), e);
                    }

                }
            }

        }
    }
}
