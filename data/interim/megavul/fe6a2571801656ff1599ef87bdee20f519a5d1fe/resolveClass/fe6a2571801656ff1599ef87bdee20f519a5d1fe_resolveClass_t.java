class resolveClass {
@Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            String name = desc.getName();
            if (allowedClasses != null && !allowedClasses.contains(name)) {
                throw new InvalidClassException("Class " + name + " isn't allowed");
            }
            return Class.forName(name, false, classLoader);
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }
}
