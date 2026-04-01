class findAll {
@SuppressFBWarnings("SQL_INJECTION_JPA")
    // The only parameter is entityName(), which is limited to either the simple class name of the entity, or its annotation
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        EntityManager entityManager = entityManagerProvider.get();
        TypedQuery<E> query = entityManager.createQuery("SELECT e FROM " + entityName(entityClass) + " e", entityClass);
        query.setFlushMode(FlushModeType.AUTO);
        return query.getResultList();
    }
}
