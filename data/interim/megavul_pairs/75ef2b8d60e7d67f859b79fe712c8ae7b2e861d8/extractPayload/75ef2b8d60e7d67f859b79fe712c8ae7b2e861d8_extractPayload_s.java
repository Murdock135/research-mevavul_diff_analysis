class extractPayload {
private Object extractPayload(Object payload, String eventClass) {
        try {
            final Class<?> clazz = chainingClassLoader.loadClass(eventClass);
            return objectMapper.convertValue(payload, clazz);
        } catch (ClassNotFoundException e) {
            LOG.debug("Couldn't load class <" + eventClass + "> for event", e);
            return null;
        } catch (IllegalArgumentException e) {
            LOG.debug("Error while deserializing payload", e);
            return null;

        }
    }
}
