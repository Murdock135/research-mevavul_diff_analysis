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
        authPlugin = (AuthenticationPlugin) ObjectFactory.instantiate(authPluginClassName, info,
            false, null);
      } catch (Exception ex) {
        LOGGER.log(Level.FINE, "Unable to load Authentication Plugin " + ex.toString());
        throw new PSQLException(ex.getMessage(), PSQLState.UNEXPECTED_ERROR);
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
