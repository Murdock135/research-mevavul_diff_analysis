class afterCreate {
@Override
    protected void afterCreate(Record record) {
        // 业务实体才验证
        if (MetadataHelper.isBusinessEntity(entity)) verify(record);
        EntityHelper.bindCommonsFieldsValue(record, record.getPrimary() == null);
    }
}
