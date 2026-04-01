class generateQuickCode_1 {
public static String generateQuickCode(Record record) {
        Entity entity = record.getEntity();
        if (!entity.containsField(EntityHelper.QuickCode)) return null;

        Field nameField = entity.getNameField();
        if (!record.hasValue(nameField.getName(), Boolean.FALSE)) return null;

        Object nameValue = record.getObjectValue(nameField.getName());
        DisplayType dt = EasyMetaFactory.getDisplayType(nameField);
        if (dt == DisplayType.TEXT || dt == DisplayType.SERIES
                || dt == DisplayType.EMAIL || dt == DisplayType.PHONE || dt == DisplayType.URL
                || dt == DisplayType.NUMBER || dt == DisplayType.DECIMAL) {
            nameValue = nameValue.toString();
        } else if (dt == DisplayType.PICKLIST) {
            nameValue = PickListManager.instance.getLabel((ID) nameValue);
        } else if (dt == DisplayType.STATE) {
            StateSpec state = StateManager.instance.findState(nameField, nameValue);
            nameValue = Language.L(state);
        } else if (dt == DisplayType.CLASSIFICATION) {
            nameValue = ClassificationManager.instance.getFullName((ID) nameValue);
        } else if (dt == DisplayType.DATE || dt == DisplayType.DATETIME) {
            nameValue = CalendarUtils.getPlainDateFormat().format(nameValue);
        } else if (dt == DisplayType.LOCATION) {
            nameValue = nameValue.toString().split(CommonsUtils.COMM_SPLITER_RE)[0];
        } else {
            nameValue = null;
        }

        if (nameValue == null) return null;
        return generateQuickCode((String) nameValue);
    }
}
