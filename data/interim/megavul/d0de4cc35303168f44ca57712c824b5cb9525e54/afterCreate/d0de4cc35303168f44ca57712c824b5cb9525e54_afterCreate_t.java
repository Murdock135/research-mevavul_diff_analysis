class afterCreate {
@Override
    protected void afterCreate(Record record) {
        int ec = entity.getEntityCode();
        // 记录验证
        if (MetadataHelper.isBusinessEntity(entity)) {
            verify(record);
        } else if (ec == EntityHelper.Feeds || ec == EntityHelper.FeedsComment
                || ec == EntityHelper.ProjectTask || ec == EntityHelper.ProjectTaskComment
                || ec == EntityHelper.User || ec == EntityHelper.Department || ec == EntityHelper.Role || ec == EntityHelper.Team) {
            removeFieldIfSafeCheck(record);
        }
        
        EntityHelper.bindCommonsFieldsValue(record, record.getPrimary() == null);
    }
}
