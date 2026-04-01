class save {
@RequestMapping("save")
    @Csrf
    public String save(@RequestAttribute SysSite site, @SessionAttribute SysUser admin, String path, String content,
            HttpServletRequest request, ModelMap model) {
        if (CommonUtils.notEmpty(path)) {
            try {
                String suffix = CmsFileUtils.getSuffix(path);
                if (ArrayUtils.contains(safeConfigComponent.getSafeSuffix(site), suffix)) {
                    String filepath = siteComponent.getWebFilePath(site.getId(), path);
                    content = new String(VerificationUtils.base64Decode(content), Constants.DEFAULT_CHARSET);
                    if (CmsFileUtils.createFile(filepath, content)) {
                        logOperateService.save(new LogOperate(site.getId(), admin.getId(), admin.getDeptId(),
                                LogLoginService.CHANNEL_WEB_MANAGER, "save.web.webfile", RequestUtils.getIpAddress(request),
                                CommonUtils.getDate(), path));
                    } else {
                        String historyFilePath = siteComponent.getWebHistoryFilePath(site.getId(), path, true);
                        CmsFileUtils.updateFile(filepath, historyFilePath, content);
                        logOperateService.save(new LogOperate(site.getId(), admin.getId(), admin.getDeptId(),
                                LogLoginService.CHANNEL_WEB_MANAGER, "update.web.webfile", RequestUtils.getIpAddress(request),
                                CommonUtils.getDate(), path));
                    }
                } else {
                    model.addAttribute(CommonConstants.ERROR, LanguagesUtils.getMessage(CommonConstants.applicationContext,
                            request.getLocale(), "verify.custom.fileType"));
                    return CommonConstants.TEMPLATE_ERROR;
                }
            } catch (IOException e) {
                model.addAttribute(CommonConstants.ERROR, e.getMessage());
                log.error(e.getMessage(), e);
                return CommonConstants.TEMPLATE_ERROR;
            }
        }
        return CommonConstants.TEMPLATE_DONE;
    }
}
