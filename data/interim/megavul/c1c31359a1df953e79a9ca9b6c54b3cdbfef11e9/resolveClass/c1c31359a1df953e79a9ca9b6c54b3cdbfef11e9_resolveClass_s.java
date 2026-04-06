class resolveClass {
@Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
            return ClassLoaderUtil.loadClass(classLoader, desc.getName());
        }
}
