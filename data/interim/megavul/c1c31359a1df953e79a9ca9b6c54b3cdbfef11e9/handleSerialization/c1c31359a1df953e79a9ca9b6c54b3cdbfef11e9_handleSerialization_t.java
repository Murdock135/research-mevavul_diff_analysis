class handleSerialization {
protected void handleSerialization(Node node) {
            BeanDefinitionBuilder serializationConfigBuilder = createBeanBuilder(SerializationConfig.class);
            AbstractBeanDefinition beanDefinition = serializationConfigBuilder.getBeanDefinition();
            fillAttributeValues(node, serializationConfigBuilder);
            for (Node child : childElements(node)) {
                String nodeName = cleanNodeName(child);
                if ("data-serializable-factories".equals(nodeName)) {
                    handleDataSerializableFactories(child, serializationConfigBuilder);
                } else if ("portable-factories".equals(nodeName)) {
                    handlePortableFactories(child, serializationConfigBuilder);
                } else if ("serializers".equals(nodeName)) {
                    handleSerializers(child, serializationConfigBuilder);
                } else if ("java-serialization-filter".equals(nodeName)) {
                    handleJavaSerializationFilter(child, serializationConfigBuilder);
                }
            }
            configBuilder.addPropertyValue("serializationConfig", beanDefinition);
        }
}
