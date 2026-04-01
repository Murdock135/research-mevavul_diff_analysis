class setLocaleAndContentFields {
protected void setLocaleAndContentFields(DocumentReference documentReference, SolrInputDocument solrDocument,
        BaseProperty<ObjectPropertyReference> objectProperty) throws Exception
    {
        PropertyClass propertyClass = objectProperty.getPropertyClass(this.xcontextProvider.get());
        // Do the work for each locale.
        for (Locale documentLocale : getLocales(documentReference, null)) {
            solrDocument.addField(FieldUtils.LOCALES, documentLocale);

            setPropertyValue(solrDocument, objectProperty, propertyClass, documentLocale);
        }

        // We can`t rely on the schema's copyField here because we would trigger it for each locale. Doing the copy to
        // the text_general field manually.
        setPropertyValue(solrDocument, objectProperty, propertyClass, null);
    }
}
