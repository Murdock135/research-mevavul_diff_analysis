class save {
@RequestMapping(value = "save", method = RequestMethod.POST)
    @Csrf
    public String save(@RequestAttribute SysSite site, CmsContent entity, Boolean draft, CmsContentAttribute attribute,
            @ModelAttribute CmsContentParameters contentParameters, String returnUrl, HttpServletRequest request,
            HttpSession session, ModelMap model) {
        Map<String, String> config = configComponent.getConfigData(site.getId(), Config.CONFIG_CODE_SITE);
        String safeReturnUrl = config.get(LoginConfigComponent.CONFIG_RETURN_URL);
        if (ControllerUtils.isUnSafeUrl(returnUrl, site, safeReturnUrl, request)) {
            returnUrl = site.isUseStatic() ? site.getSitePath() : site.getDynamicPath();
        }
        SysUser user = ControllerUtils.getUserFromSession(session);
        CmsCategoryModel categoryModel = categoryModelService
                .getEntity(new CmsCategoryModelId(entity.getCategoryId(), entity.getModelId()));
        if (ControllerUtils.verifyNotEmpty("categoryModel", categoryModel, model)
                || ControllerUtils.verifyCustom("contribute", null == user, model)) {
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX + returnUrl;
        }
        CmsCategory category = categoryService.getEntity(entity.getCategoryId());
        if (null != category && (site.getId() != category.getSiteId() || !category.isAllowContribute())) {
            category = null;
        }
        CmsModel cmsModel = modelComponent.getMap(site).get(entity.getModelId());
        if (ControllerUtils.verifyNotEmpty("category", category, model)
                || ControllerUtils.verifyNotEmpty("model", cmsModel, model)) {
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX + returnUrl;
        }
        CmsContentAdminController.initContent(entity, cmsModel, draft, false, attribute, false, CommonUtils.getDate());
        if (null != entity.getId()) {
            CmsContent oldEntity = service.getEntity(entity.getId());
            if (null == oldEntity || ControllerUtils.verifyNotEquals("siteId", site.getId(), oldEntity.getSiteId(), model)) {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + returnUrl;
            }
            entity = service.update(entity.getId(), entity, entity.isOnlyUrl() ? CmsContentAdminController.ignoreProperties
                    : CmsContentAdminController.ignorePropertiesWithUrl);
            if (null != entity.getId()) {
                logOperateService.save(new LogOperate(site.getId(), user.getId(), LogLoginService.CHANNEL_WEB, "update.content",
                        RequestUtils.getIpAddress(request), CommonUtils.getDate(), JsonUtils.getString(entity)));
            }
        } else {
            entity.setSiteId(site.getId());
            entity.setUserId(user.getId());
            service.save(entity);
            if (CommonUtils.notEmpty(entity.getParentId())) {
                service.updateChilds(entity.getParentId(), 1);
            }
            logOperateService.save(new LogOperate(site.getId(), user.getId(), LogLoginService.CHANNEL_WEB, "save.content",
                    RequestUtils.getIpAddress(request), CommonUtils.getDate(), JsonUtils.getString(entity)));
        }
        service.saveTagAndAttribute(site.getId(), user.getId(), entity.getId(), contentParameters, cmsModel, category, attribute);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + returnUrl;
    }
}
