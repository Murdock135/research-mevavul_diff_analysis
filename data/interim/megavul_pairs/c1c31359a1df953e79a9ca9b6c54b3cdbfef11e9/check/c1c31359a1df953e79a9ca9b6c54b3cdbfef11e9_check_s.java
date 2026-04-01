class check {
@Override
        boolean check(SerializationConfig c1, SerializationConfig c2) {
            return c1 == c2 || !(c1 == null || c2 == null)
                    && nullSafeEqual(c1.getPortableVersion(), c2.getPortableVersion())
                    && nullSafeEqual(c1.getDataSerializableFactoryClasses(), c2.getDataSerializableFactoryClasses())
                    && nullSafeEqual(c1.getPortableFactoryClasses(), c2.getPortableFactoryClasses())
                    && isCompatible(c1.getGlobalSerializerConfig(), c2.getGlobalSerializerConfig())
                    && isCollectionCompatible(c1.getSerializerConfigs(), c2.getSerializerConfigs(), new SerializerConfigChecker())
                    && nullSafeEqual(c1.isCheckClassDefErrors(), c2.isCheckClassDefErrors())
                    && nullSafeEqual(c1.isUseNativeByteOrder(), c2.isUseNativeByteOrder())
                    && nullSafeEqual(c1.getByteOrder(), c2.getByteOrder())
                    && nullSafeEqual(c1.isEnableCompression(), c2.isEnableCompression())
                    && nullSafeEqual(c1.isEnableSharedObject(), c2.isEnableSharedObject())
                    && nullSafeEqual(c1.isAllowUnsafe(), c2.isAllowUnsafe());
        }
}
