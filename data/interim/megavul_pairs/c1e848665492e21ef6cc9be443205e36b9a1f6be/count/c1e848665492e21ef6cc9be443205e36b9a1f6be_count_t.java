class count {
@SuppressFBWarnings("SQL_INJECTION_JPA")
    // The only parameter is entityName(), which is limited to either the simple class name of the entity, or its annotation
    @Transactional
    @Override
    public <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        EntityManager entityManager = entityManagerProvider.get();
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM " + entityName(entityClass) + " c");
        return (long) query.getSingleResult();
    }
}
