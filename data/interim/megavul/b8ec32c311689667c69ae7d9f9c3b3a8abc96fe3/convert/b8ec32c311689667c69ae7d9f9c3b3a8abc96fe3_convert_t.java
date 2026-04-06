class convert {
@Override
    public Optional<CorsOriginConfiguration> convert(Object object, Class<CorsOriginConfiguration> targetType, ConversionContext context) {
        CorsOriginConfiguration configuration = new CorsOriginConfiguration();
        if (object instanceof Map) {
            Map mapConfig = (Map) object;
            ConvertibleValues<Object> convertibleValues = new ConvertibleValuesMap<>(mapConfig);

            convertibleValues
                .get(ALLOWED_ORIGINS, ConversionContext.LIST_OF_STRING)
                .ifPresent(configuration::setAllowedOrigins);

            convertibleValues
                .get(ALLOWED_METHODS, CONVERSION_CONTEXT_LIST_OF_HTTP_METHOD)
                .ifPresent(configuration::setAllowedMethods);

            convertibleValues
                .get(ALLOWED_HEADERS, ConversionContext.LIST_OF_STRING)
                .ifPresent(configuration::setAllowedHeaders);

            convertibleValues
                .get(EXPOSED_HEADERS, ConversionContext.LIST_OF_STRING)
                .ifPresent(configuration::setExposedHeaders);

            convertibleValues
                .get(ALLOW_CREDENTIALS, ConversionContext.BOOLEAN)
                .ifPresent(configuration::setAllowCredentials);

            convertibleValues
                .get(MAX_AGE, ConversionContext.LONG)
                .ifPresent(configuration::setMaxAge);
        }
        return Optional.of(configuration);
    }
}
