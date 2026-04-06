class provideOptionDao {
@Override
    public DefaultJpaInstanceConfiguration provideOptionDao() {
        bind(OptionDao.class, DefaultJpaOptionDao.class);
        provideOptionDao = true;
        return this;
    }
}
