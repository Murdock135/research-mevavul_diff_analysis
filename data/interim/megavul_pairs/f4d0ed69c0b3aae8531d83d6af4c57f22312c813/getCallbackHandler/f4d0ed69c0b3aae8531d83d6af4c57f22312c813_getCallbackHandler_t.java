class getCallbackHandler {
private CallbackHandler getCallbackHandler(
      @UnderInitialization(WrappedFactory.class) LibPQFactory this,
      Properties info) throws PSQLException {
    // Determine the callback handler
    CallbackHandler cbh;
    String sslpasswordcallback = PGProperty.SSL_PASSWORD_CALLBACK.get(info);
    if (sslpasswordcallback != null) {
      try {
        cbh = ObjectFactory.instantiate(CallbackHandler.class, sslpasswordcallback, info, false, null);
      } catch (Exception e) {
        throw new PSQLException(
          GT.tr("The password callback class provided {0} could not be instantiated.",
            sslpasswordcallback),
          PSQLState.CONNECTION_FAILURE, e);
      }
    } else {
      cbh = new ConsoleCallbackHandler(PGProperty.SSL_PASSWORD.get(info));
    }
    return cbh;
  }
}
