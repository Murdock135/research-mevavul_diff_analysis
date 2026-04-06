class withPassword {
public static <T> T withPassword(AuthenticationRequestType type, Properties info,
      PasswordAction<char @Nullable [], T> action) throws PSQLException, IOException {
    char[] password = null;

    String authPluginClassName = PGProperty.AUTHENTICATION_PLUGIN_CLASS_NAME.get(info);

    if (authPluginClassName == null || authPluginClassName.equals("")) {
      // Default auth plugin simply pulls password directly from connection properties
      String passwordText = PGProperty.PASSWORD.get(info);
      if (passwordText != null) {
        password = passwordText.toCharArray();
      }
    } else {
      AuthenticationPlugin authPlugin;
      try {
        authPlugin = ObjectFactory.instantiate(AuthenticationPlugin.class, authPluginClassName, info,
            false, null);
      } catch (Exception ex) {
        String msg = GT.tr("Unable to load Authentication Plugin {0}", authPluginClassName);
        LOGGER.log(Level.FINE, msg, ex);
        throw new PSQLException(msg, PSQLState.INVALID_PARAMETER_VALUE, ex);
      }

      password = authPlugin.getPassword(type);
    }

    try {
      return action.apply(password);
    } finally {
      if (password != null) {
        java.util.Arrays.fill(password, (char) 0);
      }
    }
  }
}
