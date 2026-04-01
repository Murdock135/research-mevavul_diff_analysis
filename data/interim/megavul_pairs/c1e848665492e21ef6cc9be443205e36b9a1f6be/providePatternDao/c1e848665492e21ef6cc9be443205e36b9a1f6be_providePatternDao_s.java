class providePatternDao {
@Override
    public DefaultJpaInstanceConfiguration providePatternDao() {
        bind(PatternDao.class, DefaultJpaPatternDao_LongInt.class);
        providePatternDao = true;
        return this;
    }
}
