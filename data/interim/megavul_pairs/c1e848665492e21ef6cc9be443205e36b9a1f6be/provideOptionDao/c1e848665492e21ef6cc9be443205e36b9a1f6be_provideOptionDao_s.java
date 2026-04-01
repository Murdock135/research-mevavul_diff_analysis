class provideOptionDao {
@Override
    public DefaultJpaInstanceConfiguration provideOptionDao() {
        bind(OptionDao.class, DefaultOptionJpaDao_LongInt.class);
        provideOptionDao = true;
        return this;
    }
}
