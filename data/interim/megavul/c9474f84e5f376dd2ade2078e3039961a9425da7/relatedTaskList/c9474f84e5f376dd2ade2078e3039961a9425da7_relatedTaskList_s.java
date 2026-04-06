class relatedTaskList {
@RequestMapping("tasks/related-list")
    public JSON relatedTaskList(@IdParam(name = "related", required = false) ID relatedId,
                                @IdParam(name = "task", required = false) ID taskId,
                                HttpServletRequest request) {
        Assert.isTrue(relatedId != null || taskId != null, Language.L("无效请求参数"));

        final ID user = getRequestUser(request);
        String queryWhere = String.format("relatedRecord = '%s'", relatedId);

        // 关键词搜索
        String search = getParameter(request, "search");
        if (StringUtils.isNotBlank(search)) {
            queryWhere += " and taskName like '%" + StringEscapeUtils.escapeSql(search) + "%'";
        }

        int pageNo = getIntParameter(request, "pageNo", 1);
        int pageSize = getIntParameter(request, "pageSize", 40);

        queryWhere += " order by " + buildQuerySort(request);

        // 获取指定任务的（其他条件忽略）
        if (taskId != null) {
            queryWhere = String.format("taskId = '%s'", taskId);
        }

        Object[][] tasks = Application.createQueryNoFilter(
                String.format("select %s from ProjectTask where %s", FMT_FIELDS11, queryWhere))
                .setLimit(pageSize, pageNo * pageSize - pageSize)
                .array();

        JSONArray array = new JSONArray();
        for (Object[] o : tasks) {
            try {
                array.add(formatTask(o, user, false));
            } catch (ConfigurationException ex) {
                // FIXME 无项目权限会报错（考虑任务在相关项中是否无权限也显示）
                log.warn(ex.getLocalizedMessage());
            }
        }
        return array;
    }
}
