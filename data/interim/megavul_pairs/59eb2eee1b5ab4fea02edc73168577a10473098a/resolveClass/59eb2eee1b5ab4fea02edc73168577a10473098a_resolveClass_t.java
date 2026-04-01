class resolveClass {
protected Class<?> resolveClass(ObjectStreamClass descriptor) throws ClassNotFoundException, IOException {
        String className = descriptor.getName();
        if (isNullOrEmpty(className) && !isWhiteListed(className)) {
            throw new InvalidClassException("Unauthorized deserialization attempt", descriptor.getName());
        } else {
            return super.resolveClass(descriptor);
        }
    }
}
