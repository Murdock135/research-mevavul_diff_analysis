class read_1 {
private Object read(InputStream in, ClassLoader classLoader) throws IOException {
            try {
                ObjectInputStream objectInputStream = newObjectInputStream(classLoader, classFilter, in);
                if (shared) {
                    return objectInputStream.readObject();
                }
                return objectInputStream.readUnshared();
            } catch (ClassNotFoundException e) {
                throw new HazelcastSerializationException(e);
            }
        }
}
