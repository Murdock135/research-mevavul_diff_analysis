class queryListOfConfig {
public static Object[][] queryListOfConfig(String sql, String belongEntity, String q) {
        if (StringUtils.isNotBlank(belongEntity) && !"$ALL$".equalsIgnoreCase(belongEntity)) {
            sql = sql.replace("(1=1)", "belongEntity = '" + StringEscapeUtils.escapeSql(belongEntity) + "'");
        }
        if (StringUtils.isNotBlank(q)) {
            sql = sql.replace("(2=2)", "name like '%" + StringEscapeUtils.escapeSql(q) + "%'");
        }

        Object[][] array = Application.createQuery(sql).setLimit(500).array();
        for (Object[] o : array) {
            o[2] = EasyMetaFactory.getLabel(MetadataHelper.getEntity((String) o[2]));
            o[5] = I18nUtils.formatDate((Date) o[5]);
        }
        return array;
    }
}
