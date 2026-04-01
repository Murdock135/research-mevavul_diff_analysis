class createCategoryFile {
public boolean createCategoryFile(SysSite site, CmsCategory entity, Integer pageIndex, Integer totalPage) {
        if (entity.isOnlyUrl()) {
            categoryService.updateUrl(entity.getId(), entity.getPath(), false);
        } else if (CommonUtils.notEmpty(entity.getPath())) {
            try {
                if (site.isUseStatic() && CommonUtils.notEmpty(entity.getTemplatePath())) {
                    String url = site.getSitePath()
                            + createCategoryFile(site, entity, entity.getTemplatePath(), entity.getPath(), pageIndex, totalPage);
                    categoryService.updateUrl(entity.getId(), url, true);
                } else {
                    Map<String, Object> model = new HashMap<>();
                    initCategoryUrl(site, entity);
                    model.put("category", entity);
                    model.put(AbstractFreemarkerView.CONTEXT_SITE, site);
                    String filePath = FreeMarkerUtils.generateStringByString(entity.getPath(), webConfiguration, model);
                    String url;
                    if (filePath.contains("://") || filePath.startsWith("//")) {
                        url = filePath;
                    } else {
                        url = site.getDynamicPath() + filePath;
                    }
                    categoryService.updateUrl(entity.getId(), url, false);
                }
            } catch (IOException | TemplateException e) {
                log.error(e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;

    }
}
