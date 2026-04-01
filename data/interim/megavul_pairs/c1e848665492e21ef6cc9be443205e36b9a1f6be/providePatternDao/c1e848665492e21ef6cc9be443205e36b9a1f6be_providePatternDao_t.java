class providePatternDao {
@Override
    public DefaultJpaInstanceConfiguration providePatternDao() {
        bind(PatternDao.class, DefaultJpaPatternDao.class);
        providePatternDao = true;
        return this;
    }
}
