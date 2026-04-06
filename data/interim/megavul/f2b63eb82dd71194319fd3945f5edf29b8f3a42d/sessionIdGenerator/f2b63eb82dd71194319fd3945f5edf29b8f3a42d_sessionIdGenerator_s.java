class sessionIdGenerator {
@Provides
  SessionIdGenerator sessionIdGenerator() {
    return new DefaultSessionIdGenerator();
  }
}
