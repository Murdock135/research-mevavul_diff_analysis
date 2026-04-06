class createSerializationService {
protected InternalSerializationService createSerializationService(InputOutputFactory inputOutputFactory,
                                                                      Supplier<RuntimeException> notActiveExceptionSupplier) {
        switch (version) {
            case 1:
                SerializationServiceV1 serializationServiceV1 = new SerializationServiceV1(inputOutputFactory, version,
                        portableVersion, classLoader, dataSerializableFactories, portableFactories, managedContext,
                        partitioningStrategy, initialOutputBufferSize, new BufferPoolFactoryImpl(), enableCompression,
                        enableSharedObject, notActiveExceptionSupplier);
                serializationServiceV1.registerClassDefinitions(classDefinitions, checkClassDefErrors);
                return serializationServiceV1;

            // future version note: add new versions here by adding cases for each version and instantiate it properly
            default:
                throw new IllegalArgumentException("Serialization version is not supported!");
        }
    }
}
