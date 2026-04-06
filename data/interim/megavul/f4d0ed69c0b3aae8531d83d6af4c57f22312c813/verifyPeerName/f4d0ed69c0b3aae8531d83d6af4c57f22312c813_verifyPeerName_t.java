class verifyPeerName {
private static void verifyPeerName(PGStream stream, Properties info, SSLSocket newConnection)
      throws PSQLException {
    HostnameVerifier hvn;
    String sslhostnameverifier = PGProperty.SSL_HOSTNAME_VERIFIER.get(info);
    if (sslhostnameverifier == null) {
      hvn = PGjdbcHostnameVerifier.INSTANCE;
      sslhostnameverifier = "PgjdbcHostnameVerifier";
    } else {
      try {
        hvn = instantiate(HostnameVerifier.class, sslhostnameverifier, info, false, null);
      } catch (Exception e) {
        throw new PSQLException(
            GT.tr("The HostnameVerifier class provided {0} could not be instantiated.",
                sslhostnameverifier),
            PSQLState.CONNECTION_FAILURE, e);
      }
    }

    if (hvn.verify(stream.getHostSpec().getHost(), newConnection.getSession())) {
      return;
    }

    throw new PSQLException(
        GT.tr("The hostname {0} could not be verified by hostnameverifier {1}.",
            stream.getHostSpec().getHost(), sslhostnameverifier),
        PSQLState.CONNECTION_FAILURE);
  }
}
