class isForceCreateable {
private boolean isForceCreateable(Field field) {
        if (isDtmField(field)) return true;

        // 自定定位
        EasyField easyField = EasyMetaFactory.valueOf(field);
        if (easyField.getDisplayType() == DisplayType.LOCATION) {
            return BooleanUtils.toBoolean(easyField.getExtraAttr(EasyFieldConfigProps.LOCATION_AUTOLOCATION));
        }

        return false;
    }
}
