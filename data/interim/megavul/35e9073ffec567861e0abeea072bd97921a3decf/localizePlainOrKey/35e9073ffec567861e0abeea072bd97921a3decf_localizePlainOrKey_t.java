class localizePlainOrKey {
protected String localizePlainOrKey(String key, Object... parameters)
    {
        return XMLUtils.escape(StringUtils.defaultString(getLocalization().getTranslationPlain(key, parameters), key));
    }
}
