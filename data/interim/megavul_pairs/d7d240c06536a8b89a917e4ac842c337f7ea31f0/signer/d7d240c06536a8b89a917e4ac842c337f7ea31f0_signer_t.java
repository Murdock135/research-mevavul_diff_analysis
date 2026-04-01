class signer {
@Provides
  @Singleton
  Signer signer(ClientSideSessionConfig config) {
    byte[] token = config.getSecretToken().getBytes(CharsetUtil.ISO_8859_1);
    return new DefaultSigner(new SecretKeySpec(token, config.getMacAlgorithm()));
  }
}
