class find {
public Map<String, Object> find(int page, int pageSize, String keywords, String order, String field) {
        Map<String, Object> data = new HashMap<>();
        String searchKeywords = "";
        List<Object> searchParam = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (StringUtils.isNotEmpty(keywords)) {
            searchKeywords = " and (l.title like ? or l.plain_content like ? or l.keywords like ?)";
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            params.addAll(searchParam);
        }
        String pageSort = "l.logId desc";
        String sortField = field;
        if (order != null && !"".equals(order) && field != null && !"".equals(field)) {
            if ("id".equals(sortField)) {
                sortField = "logId";
            } else if ("typeName".equals(sortField)) {
                sortField = "typeId";
            } else if ("privacy".equals(sortField)) {
                sortField = "privacy";
            } else if ("lastUpdateDate".equals(sortField)) {
                sortField = "last_update_date";
            } else {
                sortField = "logId";
            }
            pageSort = "l." + sortField + " " + order;
        }
        params.add(ParseUtil.getFirstRecord(page, pageSize));
        params.add(pageSize);
        String sql = "select l.*,l.privacy privacy,t.typeName,l.logId as id,l.last_update_date as lastUpdateDate,t.alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId ) commentSize from " + TABLE_NAME + " l inner join user u inner join type t where u.userId=l.userId" + searchKeywords + " and t.typeid=l.typeid order by " + pageSort + " limit ?,?";
        data.put("rows", findEntry(sql, params.toArray()));
        ModelUtil.fillPageData(this, page, pageSize, "from " + TABLE_NAME + " l inner join user u where u.userId=l.userId " + searchKeywords, data, searchParam.toArray());
        return data;
    }
}
