class getInternalJsCssLib {
protected String getInternalJsCssLib(Map<String, Object> data) {
        String jsCssLink = "";
               
        // PWA: register service worker
        if (!"true".equals(getPropertyString("disablePwa"))) {
            WorkflowUserManager workflowUserManager = (WorkflowUserManager)AppUtil.getApplicationContext().getBean("workflowUserManager");
            boolean pushEnabled = !"true".equals(getPropertyString("disablePush")) && !workflowUserManager.isCurrentUserAnonymous();
            String appId = userview.getParamString("appId");
            if (appId != null && !appId.isEmpty()) {
                String userviewId = userview.getPropertyString("id");
                String key = userview.getParamString("key");
                if (key.isEmpty()) {
                    key = Userview.USERVIEW_KEY_EMPTY_VALUE;
                }
                
                boolean isEmbedded = false;
                if(data.get("embed") != null){
                    isEmbedded = (Boolean) data.get("embed");
                };
                
                String pwaOnlineNotificationMessage = ResourceBundleUtil.getMessage("pwa.onlineNow");
                String pwaOfflineNotificationMessage = ResourceBundleUtil.getMessage("pwa.offlineNow");
                String pwaLoginPromptMessage = ResourceBundleUtil.getMessage("pwa.loginPrompt");
                String pwaSyncingMessage = ResourceBundleUtil.getMessage("pwa.syncing");
                String pwaSyncFailedMessage = ResourceBundleUtil.getMessage("pwa.syncFailed");
                String pwaSyncSuccessMessage = ResourceBundleUtil.getMessage("pwa.syncSuccess");
                String buildNumber = ResourceBundleUtil.getMessage("build.number");
                
                String serviceWorkerUrl = data.get("context_path") + "/web/userview/" + appId + "/" + userviewId + "/"+key+"/serviceworker";
                jsCssLink += "<script>$(function() {"
                        + "var initPwaUtil = function(){"
                        + "PwaUtil.contextPath = '" + StringUtil.escapeString(data.get("context_path").toString(), StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.userviewKey = '" + StringUtil.escapeString(key, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.homePageLink = '" + StringUtil.escapeString(data.get("home_page_link").toString(), StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.serviceWorkerPath = '" + StringUtil.escapeString(serviceWorkerUrl, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.subscriptionApiPath = '" + StringUtil.escapeString(data.get("context_path").toString(), StringUtil.TYPE_JAVASCIPT, null) + "/web/console/profile/subscription';"
                        + "PwaUtil.pushEnabled = " + pushEnabled + ";"
                        + "PwaUtil.currentUsername = '" + StringUtil.escapeString(workflowUserManager.getCurrentUsername(), StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.onlineNotificationMessage = '" + StringUtil.escapeString(pwaOnlineNotificationMessage, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.offlineNotificationMessage = '" + StringUtil.escapeString(pwaOfflineNotificationMessage, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.loginPromptMessage = '" + StringUtil.escapeString(pwaLoginPromptMessage, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.syncingMessage = '" + StringUtil.escapeString(pwaSyncingMessage, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.syncFailedMessage = '" + StringUtil.escapeString(pwaSyncFailedMessage, StringUtil.TYPE_JAVA, null) + "';"
                        + "PwaUtil.syncSuccessMessage = '" + StringUtil.escapeString(pwaSyncSuccessMessage, StringUtil.TYPE_JAVASCIPT, null) + "';"
                        + "PwaUtil.isEmbedded = " + isEmbedded + ";"
                        + "PwaUtil.register();"
                        + "PwaUtil.init();"
                        + "};"
                        + "if (typeof PwaUtil !== \"undefined\") {initPwaUtil();} else { $(document).on(\"PwaUtil.ready\", function(){ initPwaUtil(); });}"
                        + "});</script>";
            }
        }
        return jsCssLink;
    }
}
