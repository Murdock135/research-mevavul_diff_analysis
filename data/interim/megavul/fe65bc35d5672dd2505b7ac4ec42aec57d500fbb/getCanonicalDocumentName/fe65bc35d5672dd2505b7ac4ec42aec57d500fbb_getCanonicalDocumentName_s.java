class getCanonicalDocumentName {
private String getCanonicalDocumentName(String documentName)
    {
        @SuppressWarnings("unchecked")
        EntityReferenceResolver<String> resolver = Utils.getComponent(EntityReferenceResolver.TYPE_STRING, "current");
        @SuppressWarnings("unchecked")
        EntityReferenceSerializer<String> serializer = Utils.getComponent(EntityReferenceSerializer.TYPE_STRING);
        return serializer.serialize(resolver.resolve(documentName, EntityType.DOCUMENT));
    }
}
