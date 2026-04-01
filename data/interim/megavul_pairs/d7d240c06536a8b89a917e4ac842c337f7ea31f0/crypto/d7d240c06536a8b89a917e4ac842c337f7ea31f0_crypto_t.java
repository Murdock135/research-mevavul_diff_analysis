class crypto {
@Provides
  @Singleton
  Crypto crypto(ClientSideSessionConfig config) {
    if (config.getSecretKey() == null || config.getCipherAlgorithm() == null) {
      return NoCrypto.INSTANCE;
    } else {
      return new DefaultCrypto(config.getSecretKey().getBytes(CharsetUtil.ISO_8859_1), config.getCipherAlgorithm());
    }
  }
}
