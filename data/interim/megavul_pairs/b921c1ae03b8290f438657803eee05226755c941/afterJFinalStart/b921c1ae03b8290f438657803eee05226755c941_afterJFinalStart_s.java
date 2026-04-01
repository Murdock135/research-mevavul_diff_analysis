class afterJFinalStart {
@Override
    public void afterJFinalStart() {
        FreeMarkerRender.getConfiguration().setClassForTemplateLoading(ZrLogConfig.class, com.zrlog.common.Constants.FTL_VIEW_PATH);
        super.afterJFinalStart();
        if (isInstalled()) {
            initDatabaseVersion();
        }
        SYSTEM_PROP.setProperty("zrlog.runtime.path", PathKit.getWebRootPath());
        SYSTEM_PROP.setProperty("server.info", JFinal.me().getServletContext().getServerInfo());
        JFinal.me().getServletContext().setAttribute("system", SYSTEM_PROP);
        blogProperties.put("version", BlogBuildInfoUtil.getVersion());
        blogProperties.put("buildId", BlogBuildInfoUtil.getBuildId());
        blogProperties.put("buildTime", new SimpleDateFormat("yyyy-MM-dd").format(BlogBuildInfoUtil.getTime()));
        blogProperties.put("runMode", BlogBuildInfoUtil.getRunMode());
        JFinal.me().getServletContext().setAttribute("zrlog", blogProperties);
        JFinal.me().getServletContext().setAttribute("config", this);
        if (haveSqlUpdated) {
            int updatedVersion = ZrLogUtil.getSqlVersion(getUpgradeSqlBasePath());
            if (updatedVersion > 0) {
                new WebSite().updateByKV(com.zrlog.common.Constants.ZRLOG_SQL_VERSION_KEY, updatedVersion + "");
            }
        }
    }
}
