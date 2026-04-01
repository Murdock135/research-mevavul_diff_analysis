class withEncodedPassword {
public static <T> T withEncodedPassword(AuthenticationRequestType type, Properties info,
      PasswordAction<byte[], T> action) throws PSQLException, IOException {
    byte[] encodedPassword = withPassword(type, info, password -> {
      if (password == null) {
        throw new PSQLException(
            GT.tr("The server requested password-based authentication, but no password was provided."),
            PSQLState.CONNECTION_REJECTED);
      }
      ByteBuffer buf = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
      byte[] bytes = new byte[buf.limit()];
      buf.get(bytes);
      return bytes;
    });

    try {
      return action.apply(encodedPassword);
    } finally {
      java.util.Arrays.fill(encodedPassword, (byte) 0);
    }
  }
}
