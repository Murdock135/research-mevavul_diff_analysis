class addCurrentElementToCollection {
protected void addCurrentElementToCollection(HierarchicalStreamReader reader, UnmarshallingContext context,
        Collection collection, Collection target) {
        final Object item = readItem(reader, context, collection); // call readBareItem when deprecated method is removed
        target.add(item);
    }
}
