class getCanonicalDocumentName {
private String getCanonicalDocumentName(String documentName)
    {
        EntityReferenceResolver<String> resolver = getCurrentEntityReferenceResolver();
        EntityReferenceSerializer<String> serializer = getDefaultEntityReferenceSerializer();
        return serializer.serialize(resolver.resolve(documentName, EntityType.DOCUMENT));
    }
}
