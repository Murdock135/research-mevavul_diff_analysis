class createSerializationService {
protected InternalSerializationService createSerializationService(InputOutputFactory inputOutputFactory,
                                                                      Supplier<RuntimeException> notActiveExceptionSupplier) {
        switch (version) {
            case 1:
                SerializationServiceV1 serializationServiceV1 = SerializationServiceV1.builder()
                    .withInputOutputFactory(inputOutputFactory)
                    .withVersion(version)
                    .withPortableVersion(portableVersion)
                    .withClassLoader(classLoader)
                    .withDataSerializableFactories(dataSerializableFactories)
                    .withPortableFactories(portableFactories)
                    .withManagedContext(managedContext)
                    .withGlobalPartitionStrategy(partitioningStrategy)
                    .withInitialOutputBufferSize(initialOutputBufferSize)
                    .withBufferPoolFactory(new BufferPoolFactoryImpl())
                    .withEnableCompression(enableCompression)
                    .withEnableSharedObject(enableSharedObject)
                    .withNotActiveExceptionSupplier(notActiveExceptionSupplier)
                    .withClassNameFilter(classNameFilter)
                    .build();
                serializationServiceV1.registerClassDefinitions(classDefinitions, checkClassDefErrors);
                return serializationServiceV1;

            // future version note: add new versions here by adding cases for each version and instantiate it properly
            default:
                throw new IllegalArgumentException("Serialization version is not supported!");
        }
    }
}
