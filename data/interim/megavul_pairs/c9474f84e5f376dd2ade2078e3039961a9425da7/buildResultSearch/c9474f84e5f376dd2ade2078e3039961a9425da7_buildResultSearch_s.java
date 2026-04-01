class buildResultSearch {
private JSON buildResultSearch(Entity searchEntity, String quickFields, String q, String appendWhere, int maxResults) {
        String searchWhere = "(1=1)";

        if (StringUtils.isNotBlank(q)) {
            // 查询字段
            Set<String> searchFields = ParseHelper.buildQuickFields(searchEntity, quickFields);
            if (searchFields.isEmpty()) {
                return JSONUtils.EMPTY_ARRAY;
            }

            String like = " like '%" + StringEscapeUtils.escapeSql(q) + "%'";
            searchWhere = StringUtils.join(searchFields.iterator(), like + " or ") + like;
        }

        if (appendWhere != null) {
            searchWhere = String.format("(%s) and (%s)", appendWhere, searchWhere);
        } else {
            searchWhere = String.format("(%s)", searchWhere);
        }

        List<Object> result = resultSearch(searchWhere, searchEntity, maxResults);
        return (JSON) JSON.toJSON(result);
    }
}
