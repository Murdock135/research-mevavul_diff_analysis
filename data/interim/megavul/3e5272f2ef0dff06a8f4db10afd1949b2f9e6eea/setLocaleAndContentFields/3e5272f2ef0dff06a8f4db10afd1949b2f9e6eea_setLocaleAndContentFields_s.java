class setLocaleAndContentFields {
protected void setLocaleAndContentFields(DocumentReference documentReference, SolrInputDocument solrDocument,
        BaseProperty<ObjectPropertyReference> objectProperty) throws Exception
    {
        // Do the work for each locale.
        for (Locale documentLocale : getLocales(documentReference, null)) {
            solrDocument.addField(FieldUtils.LOCALES, documentLocale);

            solrDocument.setField(FieldUtils.getFieldName(FieldUtils.PROPERTY_VALUE, documentLocale),
                objectProperty.getValue());
        }

        // We can`t rely on the schema's copyField here because we would trigger it for each locale. Doing the copy to
        // the text_general field manually.
        solrDocument.setField(FieldUtils.getFieldName(FieldUtils.PROPERTY_VALUE, null), objectProperty.getValue());
    }
}
