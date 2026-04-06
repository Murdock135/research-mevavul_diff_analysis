class extractPayload {
private Object extractPayload(Object payload, String eventClass) {
        try {
            final Class<?> clazz = chainingClassLoader.loadClassSafely(eventClass);
            return objectMapper.convertValue(payload, clazz);
        } catch (ClassNotFoundException e) {
            LOG.debug("Couldn't load class <" + eventClass + "> for event", e);
        } catch (IllegalArgumentException e) {
            LOG.debug("Error while deserializing payload", e);
        } catch (UnsafeClassLoadingAttemptException e) {
            LOG.warn("Couldn't load class <{}>.", eventClass, e);
        }
        return null;
    }
}
