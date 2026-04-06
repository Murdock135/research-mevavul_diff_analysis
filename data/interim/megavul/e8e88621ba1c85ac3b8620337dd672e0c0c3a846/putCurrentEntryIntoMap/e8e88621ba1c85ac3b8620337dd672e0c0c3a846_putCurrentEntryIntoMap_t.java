class putCurrentEntryIntoMap {
protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context,
        Map map, Map target) {
        final Object key = readCompleteItem(reader, context, map);
        final Object value = readCompleteItem(reader, context, map);

        long now = System.currentTimeMillis();
        target.put(key, value);
        SecurityUtils.checkForCollectionDoSAttack(context, now);
    }
}
