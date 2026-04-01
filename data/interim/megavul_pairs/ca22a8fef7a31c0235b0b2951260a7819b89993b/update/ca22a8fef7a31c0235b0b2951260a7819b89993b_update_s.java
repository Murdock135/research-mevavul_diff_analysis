class update {
@Transactional
    public void update(DatabaseTypeUpdateRequest request) {
        databaseTypeDao.selectOptionalById(request.getId()).ifPresent(data -> {
            if (DatabaseTypes.has(data.getDatabaseType())) {
                throw DomainErrors.MUST_NOT_MODIFY_SYSTEM_DEFAULT_DATABASE_TYPE.exception();
            }

            DatabaseTypePojo pojo = databaseTypePojoConverter.of(request);
            try {
                databaseTypeDao.updateById(pojo);
            } catch (DuplicateKeyException e) {
                throw DomainErrors.DATABASE_TYPE_NAME_DUPLICATE.exception();
            }

            // 名称修改，下载地址修改需要删除原有的 driver
            if (!Objects.equals(request.getDatabaseType(), data.getDatabaseType())
                    || !Objects.equals(request.getJdbcDriverFileUrl(), data.getJdbcDriverFileUrl())) {
                driverResources.delete(data.getDatabaseType());
            }
        });

    }
}
