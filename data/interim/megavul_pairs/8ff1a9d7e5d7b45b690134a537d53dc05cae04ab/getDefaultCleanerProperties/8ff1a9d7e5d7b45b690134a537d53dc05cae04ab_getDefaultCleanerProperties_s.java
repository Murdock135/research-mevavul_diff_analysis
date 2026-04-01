class getDefaultCleanerProperties {
private CleanerProperties getDefaultCleanerProperties(HTMLCleanerConfiguration configuration)
    {
        CleanerProperties defaultProperties = new CleanerProperties();
        defaultProperties.setOmitUnknownTags(true);

        // HTML Cleaner uses the compact notation by default but we don't want that since:
        // - it's more work and not required since not compact notation is valid XHTML
        // - expanded elements can also be rendered fine in browsers that only support HTML.
        defaultProperties.setUseEmptyElementTags(false);

        // Wrap script and style content in CDATA blocks
        defaultProperties.setUseCdataForScriptAndStyle(true);

        // We need this for example to ignore CDATA sections not inside script or style elements.
        defaultProperties.setIgnoreQuestAndExclam(true);

        // Remove CDATA outside of script and style since according to the spec it has no effect there.
        defaultProperties.setOmitCdataOutsideScriptAndStyle(true);

        // If the caller has defined NAMESPACE_AWARE configuration property then use it, otherwise use our default.
        String param = configuration.getParameters().get(HTMLCleanerConfiguration.NAMESPACES_AWARE);
        boolean namespacesAware = (param == null) || Boolean.parseBoolean(param);
        defaultProperties.setNamespacesAware(namespacesAware);

        // Set Cleaner transformations
        defaultProperties.setCleanerTransformations(getDefaultCleanerTransformations(configuration));

        // Don't convert special HTML entities (i.e. &ocirc;, &permil;, &times;) with unicode characters they represent.
        defaultProperties.setTranslateSpecialEntities(false);

        // Use character references rather than entity references if needed (for instance if you need to parse the
        // output as XML)
        param = configuration.getParameters().get(HTMLCleanerConfiguration.USE_CHARACTER_REFERENCES);
        boolean useCharacterReferences = (param != null) && Boolean.parseBoolean(param);
        defaultProperties.setTransResCharsToNCR(useCharacterReferences);

        // Sets the HTML version from the configuration (by default 4).
        defaultProperties.setHtmlVersion(getHTMLVersion(configuration));

        // We trim values by default for all attributes but the input value attribute.
        // The only way to currently do that is to switch off this flag, and to create a dedicated TagTransformation.
        // See TrimAttributeCleanerTransformation for more information.
        defaultProperties.setTrimAttributeValues(false);

        // This flag should be set to true once https://sourceforge.net/p/htmlcleaner/bugs/221/ is fixed.
        defaultProperties.setRecognizeUnicodeChars(false);

        param = configuration.getParameters().get(HTMLCleanerConfiguration.TRANSLATE_SPECIAL_ENTITIES);
        boolean translateSpecialEntities = (param != null) && Boolean.parseBoolean(param);
        defaultProperties.setTranslateSpecialEntities(translateSpecialEntities);

        defaultProperties.setDeserializeEntities(true);

        return defaultProperties;
    }
}
