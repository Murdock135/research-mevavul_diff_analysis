class execSql {
@RequestMapping("execSql")
    @Csrf
    public String execSql(@RequestAttribute SysSite site, @SessionAttribute SysUser admin, String sql, HttpServletRequest request,
            ModelMap model) {
        if (ControllerUtils.verifyCustom("noright", !siteComponent.isMaster(site.getId()), model)) {
            return CommonConstants.TEMPLATE_ERROR;
        }
        if (sql.contains(CommonConstants.BLANK_SPACE)) {
            String type = sql.substring(0, sql.indexOf(CommonConstants.BLANK_SPACE));
            try {
                if ("update".equalsIgnoreCase(type)) {
                    model.addAttribute("result", sqlService.update(sql));
                } else if ("insert".equalsIgnoreCase(type)) {
                    model.addAttribute("result", sqlService.insert(sql));
                } else if ("delete".equalsIgnoreCase(type)) {
                    model.addAttribute("result", sqlService.delete(sql));
                } else {
                    model.addAttribute("result", JsonUtils.getString(sqlService.select(sql)));
                }
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
            model.addAttribute("sql", sql);
            logOperateService.save(new LogOperate(site.getId(), admin.getId(), LogLoginService.CHANNEL_WEB_MANAGER,
                    "execsql.site", RequestUtils.getIpAddress(request), CommonUtils.getDate(), JsonUtils.getString(model)));
        }
        return CommonConstants.TEMPLATE_DONE;
    }
}
