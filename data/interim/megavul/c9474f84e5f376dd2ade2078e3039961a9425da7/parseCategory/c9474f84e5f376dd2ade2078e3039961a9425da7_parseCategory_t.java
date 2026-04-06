class parseCategory {
protected String parseCategory(String entity, String value) {
        Entity rootEntity = MetadataHelper.getEntity(entity);
        Field categoryField = DataListCategory.instance.getFieldOfCategory(rootEntity);
        if (categoryField == null || StringUtils.isBlank(value)) return "(9=9)";

        DisplayType dt = EasyMetaFactory.getDisplayType(categoryField);
        value = CommonsUtils.escapeSql(value);

        if (dt == DisplayType.MULTISELECT) {
            return String.format("%s && %d", categoryField.getName(), ObjectUtils.toInt(value));
        } else if (dt == DisplayType.N2NREFERENCE) {
            return String.format(
                    "exists (select recordId from NreferenceItem where ^%s = recordId and belongField = '%s' and referenceId = '%s')",
                    rootEntity.getPrimaryField().getName(), categoryField.getName(), value);
        } else if (dt == DisplayType.DATETIME || dt == DisplayType.DATE) {
            String s = value + "0000-01-01 00:00:00".substring(value.length());
            String e = value + "0000-12-31 23:59:59".substring(value.length());
            if (dt == DisplayType.DATE) {
                s = s.split(" ")[0];
                e = e.split(" ")[0];
            }
            return MessageFormat.format("({0} >= ''{1}'' and {0} <= ''{2}'')", categoryField.getName(), s, e);
        }  else {
            return String.format("%s = '%s'", categoryField.getName(), value);
        }
    }
}
