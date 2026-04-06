class deleteById {
public void deleteById(Integer id) {
        databaseTypeDao.selectOptionalById(id).ifPresent(data -> {
            if (DatabaseTypes.has(data.getDatabaseType())) {
                throw DomainErrors.MUST_NOT_MODIFY_SYSTEM_DEFAULT_DATABASE_TYPE.exception();
            }
            databaseTypeDao.deleteById(id);
            driverResources.deleteByDatabaseType(data.getDatabaseType());
        });
    }
}
