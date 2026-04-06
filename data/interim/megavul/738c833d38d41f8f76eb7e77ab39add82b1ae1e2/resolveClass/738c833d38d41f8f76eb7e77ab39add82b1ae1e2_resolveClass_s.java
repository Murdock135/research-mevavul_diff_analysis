class resolveClass {
protected Class<?> resolveClass(ObjectStreamClass osClass)
            throws IOException, ClassNotFoundException {
        // fastpath: obtain cached value
        Class<?> cls = osClass.forClass();
        if (cls == null) {
            // slowpath: resolve the class
            String className = osClass.getName();

            // if it is primitive class, for example, long.class
            cls = PRIMITIVE_CLASSES.get(className);

            if (cls == null) {
                // not primitive class
                // Use the first non-null ClassLoader on the stack. If null, use
                // the system class loader
                cls = Class.forName(className, true, callerClassLoader);
            }
        }
        return cls;
    }
}
