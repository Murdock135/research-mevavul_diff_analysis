class resolveClass {
@Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
            String name = desc.getName();
            if (classFilter != null) {
                classFilter.filter(name);
            }
            return ClassLoaderUtil.loadClass(classLoader, name);
        }
}
