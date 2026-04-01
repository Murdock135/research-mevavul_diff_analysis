class setStaticListPropertyValue {
private void setStaticListPropertyValue(SolrInputDocument solrDocument, BaseProperty<?> property,
        StaticListClass propertyClass, Locale locale)
    {
        // The list of known values specified in the XClass.
        Map<String, ListItem> knownValues = propertyClass.getMap(this.xcontextProvider.get());
        Object propertyValue = property.getValue();
        // When multiple selection is on the value is a list. Otherwise, for single selection, the value is a string.
        List<?> rawValues = propertyValue instanceof List ? (List<?>) propertyValue : Arrays.asList(propertyValue);
        for (Object rawValue : rawValues) {
            // Avoid indexing null values.
            if (rawValue != null) {
                // Index the raw value that is saved in the database. This is most probably a string so we'll be able to
                // perform exact matches on this value.
                setPropertyValue(solrDocument, property, new TypedValue(rawValue), locale);
                ListItem valueInfo = knownValues.get(rawValue);
                if (valueInfo != null && valueInfo.getValue() != null && !valueInfo.getValue().equals(rawValue)) {
                    // Index the display value as text (based on the given locale). This is the text seen by the user
                    // when he edits the static list property. This text is specified on the XClass (but can be
                    // overwritten by translations!).
                    setPropertyValue(solrDocument, property, new TypedValue(valueInfo.getValue(), TypedValue.TEXT),
                        locale);
                }
            }
        }
    }
}
