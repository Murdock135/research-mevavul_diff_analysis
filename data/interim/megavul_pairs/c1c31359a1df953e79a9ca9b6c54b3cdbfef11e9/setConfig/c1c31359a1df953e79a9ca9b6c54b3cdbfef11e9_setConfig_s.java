class setConfig {
@Override
    public SerializationServiceBuilder setConfig(SerializationConfig config) {
        this.config = config;
        if (portableVersion < 0) {
            portableVersion = config.getPortableVersion();
        }
        checkClassDefErrors = config.isCheckClassDefErrors();
        useNativeByteOrder = config.isUseNativeByteOrder();
        byteOrder = config.getByteOrder();
        enableCompression = config.isEnableCompression();
        enableSharedObject = config.isEnableSharedObject();
        allowUnsafe = config.isAllowUnsafe();
        return this;
    }
}
