class isEnumType {
@Override
    public final boolean isEnumType() {
        // 29-Sep-2019, tatu: `Class.isEnum()` not enough to detect custom subtypes,
        //   but for some reason this fix will break couple of unit tests:
        // See [databind#2480]:
//        return ClassUtil.isEnumType(_class);
        return _class.isEnum();
    }
}
