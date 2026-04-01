class signer {
@Provides
  @Singleton
  Signer signer(ClientSideSessionConfig config) {
    byte[] token = config.getSecretToken().getBytes(CharsetUtil.UTF_8);
    return new DefaultSigner(new SecretKeySpec(token, config.getMacAlgorithm()));
  }
}
