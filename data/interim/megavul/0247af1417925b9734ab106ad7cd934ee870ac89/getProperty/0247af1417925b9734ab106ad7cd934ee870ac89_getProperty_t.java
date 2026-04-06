class getProperty {
@Override
    protected <T> T getProperty(String key, Class<T> valueClass)
    {
        if (SAFE_PROPERTIES.contains(key)) {
            // Get property from request
            String requestValue = getRequestParameter(key);
            if (requestValue != null) {
                return this.converter.convert(valueClass, requestValue);
            }
        }

        // Get property from session
        T sessionValue = getSessionAttribute(key);
        if (sessionValue != null) {
            return sessionValue;
        }

        // Get property from configuration
        return this.configuration.getProperty(key, valueClass);
    }
}
