class resolveClass {
@Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            String name = desc.getName();
            return Class.forName(name, false, classLoader);
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }
}
