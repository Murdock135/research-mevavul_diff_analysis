class disconnectFromSingleController {
public boolean disconnectFromSingleController(KieServerInfo serverInfo, KieServerConfig config, String controllerUrl) {
        String connectAndSyncUrl = null;
        try {
            connectAndSyncUrl = controllerUrl + "/server/" + KieServerEnvironment.getServerId()+"/?location="+ URLEncoder.encode(serverInfo.getLocation(), "UTF-8");

            String userName = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_USER, "kieserver");
            String password = loadPassword(config);
            String token = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_TOKEN);

            makeHttpDeleteRequestAndCreateCustomResponse(connectAndSyncUrl, null, userName, password, token);


            return true;
        } catch (Exception e) {
            // let's check all other controllers in case of running in cluster of controllers
            logger.debug("Exception encountered while syncing with controller at {} error {}", connectAndSyncUrl, e.getMessage(), e);
            
            return false;
        }
    }
}
