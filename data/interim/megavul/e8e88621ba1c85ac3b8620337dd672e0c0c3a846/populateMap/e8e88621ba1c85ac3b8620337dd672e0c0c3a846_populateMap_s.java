class populateMap {
protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context,
        Map map, Map target) {
        SingleValueConverter keyConverter = null;
        SingleValueConverter valueConverter = null;
        if (keyAsAttribute) {
            keyConverter = getSingleValueConverter(keyType, "key");
        }
        if (valueAsAttribute || valueName == null) {
            valueConverter = getSingleValueConverter(valueType, "value");
        }

        while (reader.hasMoreChildren()) {
            Object key = null;
            Object value = null;

            if (entryName != null) {
                reader.moveDown();

                if (keyConverter != null) {
                    String attribute = reader.getAttribute(keyName);
                    if (attribute != null) {
                        key = keyConverter.fromString(attribute);
                    }
                }

                if (valueAsAttribute && valueConverter != null) {
                    String attribute = reader.getAttribute(valueName);
                    if (attribute != null) {
                        value = valueConverter.fromString(attribute);
                    }
                }
            }

            if (keyConverter == null) {
                reader.moveDown();
                if (valueConverter == null
                    && !keyName.equals(valueName)
                    && reader.getNodeName().equals(valueName)) {
                    value = readItem(valueType, reader, context, map);
                } else {
                    key = readItem(keyType, reader, context, map);
                }
                reader.moveUp();
            }

            if (valueConverter == null) {
                reader.moveDown();
                if (keyConverter == null && key == null && value != null) {
                    key = readItem(keyType, reader, context, map);
                } else {
                    value = readItem(valueType, reader, context, map);
                }
                reader.moveUp();
            } else if (!valueAsAttribute) {
                value = valueConverter.fromString(reader.getValue());
            }

            target.put(key, value);

            if (entryName != null) {
                reader.moveUp();
            }
        }
    }
}
