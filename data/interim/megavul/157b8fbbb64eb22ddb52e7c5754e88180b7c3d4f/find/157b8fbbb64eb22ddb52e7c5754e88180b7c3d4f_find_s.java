class find {
public Map<String, Object> find(int page, int pageSize, String keywords, String order, String field) {
        Map<String, Object> data = new HashMap<>();
        String searchKeywords = "";
        if (keywords != null && !"".equals(keywords)) {
            searchKeywords = " and (l.title like '%" + keywords + "%' or l.plain_content like '%" + keywords + "%' or l.keywords like '%" + keywords + "%')";
        }
        String pageSort = "l.logId desc";
        String copyField = field;
        if (order != null && !"".equals(order) && field != null && !"".equals(field)) {
            if ("id".equals(copyField)) {
                copyField = "logId";
            } else if ("typeName".equals(copyField)) {
                copyField = "typeId";
            } else if ("privacy".equals(copyField)) {
                copyField = "privacy";
            } else if ("lastUpdateDate".equals(copyField)) {
                copyField = "last_update_date";
            }
            pageSort = "l." + copyField + " " + order;
        }
        String sql = "select l.*,l.privacy privacy,t.typeName,l.logId as id,l.last_update_date as lastUpdateDate,t.alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId ) commentSize from " + TABLE_NAME + " l inner join user u inner join type t where u.userId=l.userId" + searchKeywords + " and t.typeid=l.typeid order by " + pageSort + " limit ?,?";
        data.put("rows", findEntry(sql, ParseUtil.getFirstRecord(page, pageSize), pageSize));
        ModelUtil.fillPageData(this, page, pageSize, "from " + TABLE_NAME + " l inner join user u where u.userId=l.userId " + searchKeywords, data, new Object[]{});
        return data;
    }
}
