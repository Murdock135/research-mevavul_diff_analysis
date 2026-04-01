class isForceCreateable {
private boolean isForceCreateable(Field field) {
        // DTF 字段
        if (isDtmField(field)) return true;

        // 自动定位的
        EasyField easyField = EasyMetaFactory.valueOf(field);
        if (easyField.getDisplayType() == DisplayType.LOCATION) {
            return BooleanUtils.toBoolean(easyField.getExtraAttr(EasyFieldConfigProps.LOCATION_AUTOLOCATION));
        }

        return false;
    }
}
