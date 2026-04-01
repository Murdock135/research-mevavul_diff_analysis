class execSql {
@RequestMapping("execSql")
    @Csrf
    public String execSql(@RequestAttribute SysSite site, @SessionAttribute SysUser admin, String sqlcommand,
            String[] sqlparameters, HttpServletRequest request, ModelMap model) {
        if (ControllerUtils.verifyCustom("noright", !siteComponent.isMaster(site.getId()), model)) {
            return CommonConstants.TEMPLATE_ERROR;
        }
        if ("update_url".contains(sqlcommand)) {
            if (null != sqlparameters && 2 == sqlparameters.length) {
                try {
                    String oldurl = sqlparameters[0];
                    String newurl = sqlparameters[1];
                    int i = sqlService.updateContentAttribute(oldurl, newurl);
                    i += sqlService.updateContentRelated(oldurl, newurl);
                    i += sqlService.updatePlace(oldurl, newurl);
                    i += sqlService.updatePlaceAttribute(oldurl, newurl);
                    model.addAttribute("result", i);
                } catch (Exception e) {
                    model.addAttribute("error", e.getMessage());
                }
            }
        }
        model.addAttribute("sqlcommand", sqlcommand);
        model.addAttribute("sqlparameters", sqlparameters);
        logOperateService.save(new LogOperate(site.getId(), admin.getId(), LogLoginService.CHANNEL_WEB_MANAGER,
                "execsql.site", RequestUtils.getIpAddress(request), CommonUtils.getDate(), JsonUtils.getString(model)));
        return CommonConstants.TEMPLATE_DONE;
    }
}
