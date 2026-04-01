class create {
public Integer create(DatabaseTypeCreateRequest request) {
        DatabaseTypePojo pojo = databaseTypePojoConverter.of(request);
        try {
            return databaseTypeDao.insertAndReturnId(pojo);
        } catch (DuplicateKeyException e) {
            throw DomainErrors.DATABASE_TYPE_NAME_DUPLICATE.exception();
        }
    }
}
