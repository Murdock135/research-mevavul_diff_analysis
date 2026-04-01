class convert {
@Override
    public Optional<CorsOriginConfiguration> convert(Object object, Class<CorsOriginConfiguration> targetType, ConversionContext context) {
        CorsOriginConfiguration configuration = new CorsOriginConfiguration();
        if (object instanceof Map) {
            Map mapConfig = (Map) object;
            ConvertibleValues<Object> convertibleValues = new ConvertibleValuesMap<>(mapConfig);

            convertibleValues
                .get(ALLOWED_ORIGINS, Argument.listOf(String.class))
                .ifPresent(configuration::setAllowedOrigins);

            convertibleValues
                .get(ALLOWED_METHODS, Argument.listOf(HttpMethod.class))
                .ifPresent(configuration::setAllowedMethods);

            convertibleValues
                .get(ALLOWED_HEADERS, Argument.listOf(String.class))
                .ifPresent(configuration::setAllowedHeaders);

            convertibleValues
                .get(EXPOSED_HEADERS, Argument.listOf(String.class))
                .ifPresent(configuration::setExposedHeaders);

            convertibleValues
                .get(ALLOW_CREDENTIALS, Boolean.class)
                .ifPresent(configuration::setAllowCredentials);

            convertibleValues
                .get(MAX_AGE, Long.class)
                .ifPresent(configuration::setMaxAge);
        }
        return Optional.of(configuration);
    }
}
