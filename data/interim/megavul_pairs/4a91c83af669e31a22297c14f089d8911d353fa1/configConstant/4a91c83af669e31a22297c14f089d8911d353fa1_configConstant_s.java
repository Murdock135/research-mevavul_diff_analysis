class configConstant {
@Override
    public void configConstant(Constants con) {
        con.setDevMode(BlogBuildInfoUtil.isDev());
        con.setViewType(ViewType.JSP);
        con.setEncoding("utf-8");
        con.setI18nDefaultBaseName(com.zrlog.common.Constants.I18N);
        con.setI18nDefaultLocale("zh_CN");
        con.setError404View(com.zrlog.common.Constants.NOT_FOUND_PAGE);
        con.setError500View(com.zrlog.common.Constants.ERROR_PAGE);
        con.setError403View(com.zrlog.common.Constants.FORBIDDEN_PAGE);
        con.setBaseUploadPath(PathKit.getWebRootPath() + com.zrlog.common.Constants.ATTACHED_FOLDER);
        //最大的提交的body的大小
        con.setMaxPostSize(1024 * 1024 * 1024);
    }
}
