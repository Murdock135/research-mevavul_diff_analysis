class sessionIdGenerator {
@Provides
  @Singleton
  SessionIdGenerator sessionIdGenerator() {
    return new DefaultSessionIdGenerator();
  }
}
