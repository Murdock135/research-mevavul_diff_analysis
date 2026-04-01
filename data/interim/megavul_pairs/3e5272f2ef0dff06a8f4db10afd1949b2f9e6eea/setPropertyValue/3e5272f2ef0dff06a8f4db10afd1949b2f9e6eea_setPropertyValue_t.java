class setPropertyValue {
@Override
    protected void setPropertyValue(SolrInputDocument solrDocument, BaseProperty<?> property,
        TypedValue typedValue, Locale locale)
    {
        Object value = typedValue.getValue();
        String type = typedValue.getType();

        // We need to be able to query an object property alone.
        EntityReference classReference = property.getObject().getRelativeXClassReference();
        EntityReference propertyReference =
            new EntityReference(property.getName(), EntityType.CLASS_PROPERTY, classReference);
        String serializedPropertyReference = fieldNameEncoder.encode(fieldNameSerializer.serialize(propertyReference));
        String prefix = "property." + serializedPropertyReference;
        // Note that we're using "addField" because we want to collect all the property values, even from multiple
        // objects of the same type.
        solrDocument.addField(FieldUtils.getFieldName(prefix, type, locale), value);

        // We need to be able to sort by a property value and for this we need a dedicated (single valued) field because
        // the field we just added is multiValued and multiValued fields are not sortable.
        // We don't need to sort on properties that hold large localized texts or large strings (e.g. TextArea).
        if ((type != TypedValue.TEXT && type != TypedValue.STRING)
            || String.valueOf(value).length() <= getShortTextLimit()) {
            // Short localized texts are indexed as strings because a sort field is either non-tokenized (i.e. has no
            // Analyzer) or uses an Analyzer that only produces a single Term (i.e. uses the KeywordTokenizer).
            String sortType = "sort" + StringUtils.capitalize(type == TypedValue.TEXT ? TypedValue.STRING : type);
            // We're using "setField" because the sort fields must be single valued. The consequence is that for
            // properties with multiple values the last value we set will be used for sorting (e.g. if a document has
            // two objects of the same type then the value from the second object will be used for sorting).
            solrDocument.setField(FieldUtils.getFieldName(prefix, sortType, locale), value);
        }

        // We need to be able to query all properties of a specific type of object at once.
        String serializedClassReference = fieldNameEncoder.encode(fieldNameSerializer.serialize(classReference));
        String objectOfTypeFieldName = "object." + serializedClassReference;
        // The current method can be called multiple times for the same property value (but with a different type).
        // Since we don't care about the value type here (all the values are collected in a localized field) we need to
        // make sure we don't add the same value twice.
        addFieldValueOnce(solrDocument, FieldUtils.getFieldName(objectOfTypeFieldName, locale), value);

        // We need to be able to query all objects from a document at once.
        super.setPropertyValue(solrDocument, property, typedValue, locale);
    }
}
