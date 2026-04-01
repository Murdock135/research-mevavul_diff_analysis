class handle {
@Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        //便于Wappalyzer读取
        response.addHeader("X-ZrLog", BlogBuildInfoUtil.getVersion());
        boolean isPluginPath = false;
        for (String path : pluginHandlerPaths) {
            if (target.startsWith(path)) {
                isPluginPath = true;
            }
        }
        if (isPluginPath) {
            try {
                Map.Entry<AdminTokenVO, User> entry = adminTokenService.getAdminTokenVOUserEntry(request);
                if (entry != null) {
                    adminTokenService.setAdminToken(entry.getValue(), entry.getKey().getSessionId(), entry.getKey().getProtocol(), request, response);
                }
                if (target.startsWith("/admin/plugins/")) {
                    try {
                        adminPermission(target, request, response);
                    } catch (IOException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                } else if (target.startsWith("/plugin/") || target.startsWith("/p/")) {
                    try {
                        visitorPermission(target, request, response);
                    } catch (IOException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                }
            } finally {
                isHandled[0] = true;
            }
        } else {
            this.next.handle(target, request, response, isHandled);
        }
    }
}
