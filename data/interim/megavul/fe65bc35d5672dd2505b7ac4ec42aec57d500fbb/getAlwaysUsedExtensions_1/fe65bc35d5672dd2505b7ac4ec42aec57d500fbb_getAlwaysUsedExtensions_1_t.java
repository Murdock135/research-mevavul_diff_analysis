class getAlwaysUsedExtensions_1 {
@Override
    public Set<String> getAlwaysUsedExtensions(XWikiContext context)
    {
        EntityReferenceSerializer<String> serializer = getDefaultEntityReferenceSerializer();
        Set<DocumentReference> references = getAlwaysUsedExtensions();
        Set<String> names = new HashSet<>(references.size());
        for (DocumentReference reference : references) {
            names.add(serializer.serialize(reference));
        }
        return names;
    }
}
