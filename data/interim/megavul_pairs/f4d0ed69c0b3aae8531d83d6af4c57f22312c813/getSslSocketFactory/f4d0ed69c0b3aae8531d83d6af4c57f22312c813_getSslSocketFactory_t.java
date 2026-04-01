class getSslSocketFactory {
public static SSLSocketFactory getSslSocketFactory(Properties info) throws PSQLException {
    String classname = PGProperty.SSL_FACTORY.get(info);
    if (classname == null
        || "org.postgresql.ssl.jdbc4.LibPQFactory".equals(classname)
        || "org.postgresql.ssl.LibPQFactory".equals(classname)) {
      return new LibPQFactory(info);
    }
    try {
      return ObjectFactory.instantiate(SSLSocketFactory.class, classname, info, true,
          PGProperty.SSL_FACTORY_ARG.get(info));
    } catch (Exception e) {
      throw new PSQLException(
          GT.tr("The SSLSocketFactory class provided {0} could not be instantiated.", classname),
          PSQLState.CONNECTION_FAILURE, e);
    }
  }
}
