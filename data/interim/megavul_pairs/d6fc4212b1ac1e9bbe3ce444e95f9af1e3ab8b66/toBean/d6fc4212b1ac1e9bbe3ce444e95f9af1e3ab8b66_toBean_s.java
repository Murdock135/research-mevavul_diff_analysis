class toBean {
@SuppressWarnings("unchecked")
	public <T> T toBean(Class<T> beanClass) {
        setTag(new Tag(beanClass));
        
		if (getVersion() != null) {
			try {
				MigrationHelper.migrate(getVersion(), beanClass.newInstance(), this);
				removeVersion();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
        return (T) new OneYaml().construct(this);
	}
}
