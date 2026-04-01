class searchClassification {
@GetMapping("classification")
    public JSON searchClassification(@EntityParam Entity entity, HttpServletRequest request) {
        final ID user = getRequestUser(request);
        final String field = getParameterNotNull(request, "field");

        Field fieldMeta = entity.getField(field);
        ID useClassification = ClassificationManager.instance.getUseClassification(fieldMeta, false);
        if (useClassification == null) return JSONUtils.EMPTY_ARRAY;

        String q = getParameter(request, "q");

        // 为空则加载最近使用的
        if (StringUtils.isBlank(q)) {
            String type = "d" + useClassification + ":" + ClassificationManager.instance.getOpenLevel(fieldMeta);
            ID[] used = RecentlyUsedHelper.gets(user, "ClassificationData", type);

            if (used.length == 0) {
                return JSONUtils.EMPTY_ARRAY;
            } else {
                return RecentlyUsedSearchController.formatSelect2(used, null);
            }
        }

        q = StringEscapeUtils.escapeSql(q);

        int openLevel = ClassificationManager.instance.getOpenLevel(fieldMeta);
        String sqlWhere = String.format(
                "dataId = '%s' and level = %d and (fullName like '%%%s%%' or quickCode like '%%%s%%') order by fullName",
                useClassification.toLiteral(), openLevel, q, q);

        List<Object> result = resultSearch(
                sqlWhere, MetadataHelper.getEntity(EntityHelper.ClassificationData), 10);
        return (JSON) JSON.toJSON(result);
    }
}
