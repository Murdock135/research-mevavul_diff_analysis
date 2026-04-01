class list {
@Override
    public Set<Class<?>> list() {
        final ImmutableSet.Builder<Class<?>> classes = ImmutableSet.builder();

        try (DBCursor<ClusterConfig> clusterConfigs = dbCollection.find()) {
            for (ClusterConfig clusterConfig : clusterConfigs) {
                final String type = clusterConfig.type();
                try {
                    final Class<?> cls = chainingClassLoader.loadClass(type);
                    classes.add(cls);
                } catch (ClassNotFoundException e) {
                    LOG.debug("Couldn't find configuration class \"{}\"", type, e);
                }
            }
        }

        return classes.build();
    }
}
