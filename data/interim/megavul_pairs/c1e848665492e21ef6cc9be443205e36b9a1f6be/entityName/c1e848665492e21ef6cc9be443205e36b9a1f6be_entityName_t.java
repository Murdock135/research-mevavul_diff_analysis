class entityName {
@Override
    @Nonnull
    public final <E extends KrailEntity<ID, VER>> String entityName(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);

        // Get the @Entity annotation to check for name change
        Entity t = entityClass.getAnnotation(Entity.class);

        //If no Table annotation use the default (simple class name)
        return t.name()
                .isEmpty() ? entityClass.getSimpleName() : t.name();


    }
}
