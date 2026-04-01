class initCache {
private void initCache(Controller baseController) {
        BaseDataInitVO cacheInit = (BaseDataInitVO) JFinal.me().getServletContext().getAttribute(Constants.CACHE_KEY);
        if (cacheInit == null) {
            cacheInit = new BaseDataInitVO();
            Map<String, Object> website = new WebSite().getWebSite();
            //兼容早期模板判断方式
            website.put("user_comment_pluginStatus", "on".equals(website.get("duoshuo_status")));

            BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
            statistics.setTotalArticleSize(new Log().count());
            cacheInit.setStatistics(statistics);
            cacheInit.setWebSite(website);
            cacheInit.setLinks(new Link().find());
            cacheInit.setTypes(new Type().find());
            statistics.setTotalTypeSize(cacheInit.getTypes().size());
            cacheInit.setLogNavs(new LogNav().find());
            cacheInit.setPlugins(new Plugin().find());
            cacheInit.setArchives(new Log().getArchives());
            cacheInit.setTags(new Tag().find());
            statistics.setTotalTagSize(cacheInit.getTags().size());
            List<Type> types = cacheInit.getTypes();
            cacheInit.setHotLogs((List<Log>) new Log().find(1, 6).get("rows"));
            Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<>();
            for (Type type : types) {
                Map<String, Object> typeMap = new TreeMap<>();
                typeMap.put("typeName", type.getStr("typeName"));
                typeMap.put("alias", type.getStr("alias"));
                indexHotLog.put(typeMap, (List<Log>) new Log().findByTypeAlias(1, 6, type.getStr("alias")).get("rows"));
            }
            cacheInit.setIndexHotLogs(indexHotLog);
            //存放公共数据到ServletContext
            JFinal.me().getServletContext().setAttribute("WEB_SITE", website);
            JFinal.me().getServletContext().setAttribute(Constants.CACHE_KEY, cacheInit);
            List<File> staticFiles = new ArrayList<>();
            FileUtils.getAllFiles(PathKit.getWebRootPath(), staticFiles);
            for (File file : staticFiles) {
                String uri = file.toString().substring(PathKit.getWebRootPath().length());
                cacheFileMap.put(uri, file.lastModified() + "");
            }
            if(cacheInit.getTags() == null || cacheInit.getTags().isEmpty()){
                cacheInit.getPlugins().remove("tags");
            }
            if(cacheInit.getArchives() == null || cacheInit.getArchives().isEmpty()){
                cacheInit.getPlugins().remove("archives");
            }
            if(cacheInit.getTypes() == null || cacheInit.getTypes().isEmpty()){
                cacheInit.getPlugins().remove("types");
            }
            if(cacheInit.getLinks() == null || cacheInit.getLinks().isEmpty()){
                cacheInit.getPlugins().remove("links");
            }

        }
        if (baseController != null) {
            baseController.setAttr("init", cacheInit);
            baseController.setAttr("website", cacheInit.getWebSite());
            //默认开启文章封面
            cacheInit.getWebSite().putIfAbsent("article_thumbnail_status", "1");
            Constants.WEB_SITE.clear();
            Constants.WEB_SITE.putAll(cacheInit.getWebSite());
        }
    }
}
