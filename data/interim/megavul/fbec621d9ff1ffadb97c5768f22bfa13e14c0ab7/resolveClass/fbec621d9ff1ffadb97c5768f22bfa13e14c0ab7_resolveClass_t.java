class resolveClass {
@Override
        protected Class<?> resolveClass(final ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
            if (classDesc.getName().equals("[Ljava.lang.String;")) {
                return super.resolveClass(classDesc);
            }
            throw new RuntimeException(String.format("not support class: %s", classDesc.getName()));
        }
}
