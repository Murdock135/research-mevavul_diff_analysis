class resolveClass {
protected Class<?> resolveClass(java.io.ObjectStreamClass descriptor) throws ClassNotFoundException, IOException {
        String className = descriptor.getName();
        ClassFilter classFilter = new ClassFilter();
        if(className != null && className.length() > 0 && !classFilter.isWhiteListed(className)) {
            throw new InvalidClassException("Unauthorized deserialization attempt", descriptor.getName());
        } else {
            return super.resolveClass(descriptor);
        }
    }
}
