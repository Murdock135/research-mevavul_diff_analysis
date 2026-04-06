class parseSerialization {
protected SerializationConfig parseSerialization(final Node node) {
        SerializationConfig serializationConfig = new SerializationConfig();
        for (Node child : childElements(node)) {
            final String name = cleanNodeName(child);
            if ("portable-version".equals(name)) {
                String value = getTextContent(child);
                serializationConfig.setPortableVersion(getIntegerValue(name, value));
            } else if ("check-class-def-errors".equals(name)) {
                String value = getTextContent(child);
                serializationConfig.setCheckClassDefErrors(getBooleanValue(value));
            } else if ("use-native-byte-order".equals(name)) {
                serializationConfig.setUseNativeByteOrder(getBooleanValue(getTextContent(child)));
            } else if ("byte-order".equals(name)) {
                String value = getTextContent(child);
                ByteOrder byteOrder = null;
                if (ByteOrder.BIG_ENDIAN.toString().equals(value)) {
                    byteOrder = ByteOrder.BIG_ENDIAN;
                } else if (ByteOrder.LITTLE_ENDIAN.toString().equals(value)) {
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
                serializationConfig.setByteOrder(byteOrder != null ? byteOrder : ByteOrder.BIG_ENDIAN);
            } else if ("enable-compression".equals(name)) {
                serializationConfig.setEnableCompression(getBooleanValue(getTextContent(child)));
            } else if ("enable-shared-object".equals(name)) {
                serializationConfig.setEnableSharedObject(getBooleanValue(getTextContent(child)));
            } else if ("allow-unsafe".equals(name)) {
                serializationConfig.setAllowUnsafe(getBooleanValue(getTextContent(child)));
            } else if ("data-serializable-factories".equals(name)) {
                fillDataSerializableFactories(child, serializationConfig);
            } else if ("portable-factories".equals(name)) {
                fillPortableFactories(child, serializationConfig);
            } else if ("serializers".equals(name)) {
                fillSerializers(child, serializationConfig);
            } else if ("java-serialization-filter".equals(name)) {
                fillJavaSerializationFilter(child, serializationConfig);
            }
        }
        return serializationConfig;
    }
}
