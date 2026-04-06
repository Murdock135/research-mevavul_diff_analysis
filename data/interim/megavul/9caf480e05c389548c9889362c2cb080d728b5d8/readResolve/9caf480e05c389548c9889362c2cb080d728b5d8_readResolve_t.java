class readResolve {
@SuppressWarnings("unchecked")
  protected final Object readResolve() throws ObjectStreamException {
    /* Second run */
    if (this.userBean != null && this.userBeanBytes.length == 0) {
      return this.userBean;
    }

    SerialFilterChecker.check();

    /* First run */
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(this.userBeanBytes))) {
      this.userBean = in.readObject();
      this.unloadedProperties = (Map<String, ResultLoaderMap.LoadPair>) in.readObject();
      this.objectFactory = (ObjectFactory) in.readObject();
      this.constructorArgTypes = (Class<?>[]) in.readObject();
      this.constructorArgs = (Object[]) in.readObject();
    } catch (final IOException ex) {
      throw (ObjectStreamException) new StreamCorruptedException().initCause(ex);
    } catch (final ClassNotFoundException ex) {
      throw (ObjectStreamException) new InvalidClassException(ex.getLocalizedMessage()).initCause(ex);
    }

    final Map<String, ResultLoaderMap.LoadPair> arrayProps = new HashMap<>(this.unloadedProperties);
    final List<Class<?>> arrayTypes = Arrays.asList(this.constructorArgTypes);
    final List<Object> arrayValues = Arrays.asList(this.constructorArgs);

    return this.createDeserializationProxy(userBean, arrayProps, objectFactory, arrayTypes, arrayValues);
  }
}
