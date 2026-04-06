class newTomlFactory {
protected static TomlFactory newTomlFactory() {
        return TomlFactory.builder()
                .enable(TomlReadFeature.VALIDATE_NESTING_DEPTH)
                .build();
    }
}
