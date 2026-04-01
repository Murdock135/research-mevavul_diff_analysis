class count {
@Transactional
    @Override
    public <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        EntityManager entityManager = entityManagerProvider.get();
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM " + entityName(entityClass) + " c");
        return (long) query.getSingleResult();
    }
}
