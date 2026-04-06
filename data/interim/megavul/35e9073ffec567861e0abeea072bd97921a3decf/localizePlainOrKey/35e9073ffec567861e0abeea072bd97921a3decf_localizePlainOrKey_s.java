class localizePlainOrKey {
protected String localizePlainOrKey(String key, Object... parameters)
    {
        return StringUtils.defaultString(getLocalization().getTranslationPlain(key, parameters), key);
    }
}
