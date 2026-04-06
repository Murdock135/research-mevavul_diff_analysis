class toString {
@Override
    public String toString() {
        return "SerializationConfig{"
                + "portableVersion=" + portableVersion
                + ", dataSerializableFactoryClasses=" + dataSerializableFactoryClasses
                + ", dataSerializableFactories=" + dataSerializableFactories
                + ", portableFactoryClasses=" + portableFactoryClasses
                + ", portableFactories=" + portableFactories
                + ", globalSerializerConfig=" + globalSerializerConfig
                + ", serializerConfigs=" + serializerConfigs
                + ", checkClassDefErrors=" + checkClassDefErrors
                + ", classDefinitions=" + classDefinitions
                + ", byteOrder=" + byteOrder
                + ", useNativeByteOrder=" + useNativeByteOrder
                + ", javaSerializationFilterConfig=" + javaSerializationFilterConfig
                + '}';
    }
}
