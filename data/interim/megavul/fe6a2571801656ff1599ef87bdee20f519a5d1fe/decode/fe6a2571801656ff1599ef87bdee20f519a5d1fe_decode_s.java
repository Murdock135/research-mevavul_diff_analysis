class decode {
@Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            try {
                //set thread context class loader to be the classLoader variable as there could be reflection
                //done while reading from input stream which reflection will use thread class loader to load classes on demand
                ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    ByteBufInputStream in = new ByteBufInputStream(buf);
                    ObjectInputStream inputStream;
                    if (classLoader != null) {
                        Thread.currentThread().setContextClassLoader(classLoader);
                        inputStream = new CustomObjectInputStream(classLoader, in);
                    } else {
                        inputStream = new ObjectInputStream(in);
                    }
                    return inputStream.readObject();
                } finally {
                    Thread.currentThread().setContextClassLoader(currentThreadClassLoader);
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
}
