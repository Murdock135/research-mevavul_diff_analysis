class receiveHeartBeat {
@ResponseBody
    @RequestMapping("/machine")
    public Result<?> receiveHeartBeat(String app,
                                      @RequestParam(value = "app_type", required = false, defaultValue = "0")
                                          Integer appType, Long version, String v, String hostname, String ip,
                                      Integer port) {
        if (StringUtil.isBlank(app) || app.length() > 256) {
            return Result.ofFail(-1, "invalid appName");
        }
        if (StringUtil.isBlank(ip) || ip.length() > 128) {
            return Result.ofFail(-1, "invalid ip: " + ip);
        }
        if (!IPAddressUtil.isIPv4LiteralAddress(ip) && !IPAddressUtil.isIPv6LiteralAddress(ip)) {
            return Result.ofFail(-1, "invalid ip: " + ip);
        }
        if (port == null || port < -1) {
            return Result.ofFail(-1, "invalid port");
        }
        if (hostname != null && hostname.length() > 256) {
            return Result.ofFail(-1, "hostname too long");
        }
        if (port == -1) {
            logger.warn("Receive heartbeat from " + ip + " but port not set yet");
            return Result.ofFail(-1, "your port not set yet");
        }
        String sentinelVersion = StringUtil.isBlank(v) ? "unknown" : v;

        version = version == null ? System.currentTimeMillis() : version;
        try {
            MachineInfo machineInfo = new MachineInfo();
            machineInfo.setApp(app);
            machineInfo.setAppType(appType);
            machineInfo.setHostname(hostname);
            machineInfo.setIp(ip);
            machineInfo.setPort(port);
            machineInfo.setHeartbeatVersion(version);
            machineInfo.setLastHeartbeat(System.currentTimeMillis());
            machineInfo.setVersion(sentinelVersion);
            appManagement.addMachine(machineInfo);
            return Result.ofSuccessMsg("success");
        } catch (Exception e) {
            logger.error("Receive heartbeat error", e);
            return Result.ofFail(-1, e.getMessage());
        }
    }
}
