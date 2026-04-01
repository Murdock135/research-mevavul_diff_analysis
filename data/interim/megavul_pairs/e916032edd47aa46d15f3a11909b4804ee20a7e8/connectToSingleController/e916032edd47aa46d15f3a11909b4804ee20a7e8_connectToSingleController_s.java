class connectToSingleController {
public KieServerSetup connectToSingleController(KieServerInfo serverInfo, KieServerConfig config, String controllerUrl) {
        String connectAndSyncUrl = controllerUrl + "/server/" + KieServerEnvironment.getServerId();

        String userName = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_USER, "kieserver");
        String password = loadPassword(config);
        String token = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_TOKEN);

        try {
            KieServerSetup kieServerSetup = makeHttpPutRequestAndCreateCustomResponse(connectAndSyncUrl, serialize(serverInfo), KieServerSetup.class, userName, password, token);

            if (kieServerSetup != null) {
                // once there is non null list let's return it
                return kieServerSetup;

            }
            
        } catch (Exception e) {
            // let's check all other controllers in case of running in cluster of controllers
            logger.warn("Exception encountered while syncing with controller at {} error {}", connectAndSyncUrl, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            logger.debug("Exception encountered while syncing with controller at {} error {}", connectAndSyncUrl, e.getMessage(), e);
                        
        }
        return null;
    }
}
