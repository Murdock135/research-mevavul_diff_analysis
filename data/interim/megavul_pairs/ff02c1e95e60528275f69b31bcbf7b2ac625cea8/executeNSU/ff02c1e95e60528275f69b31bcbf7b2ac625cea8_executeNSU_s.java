class executeNSU {
@Test
  public void executeNSU() throws Exception {
    final DeferredGroupException dge = mock(DeferredGroupException.class);
    when(dge.getCause()).thenReturn(new NoSuchUniqueName("foo", "metrics"));

    when(query_result.configureFromQuery((TSQuery)any(), anyInt()))
      .thenReturn(Deferred.fromError(dge));

    final HttpQuery query = NettyMocks.getQuery(tsdb,
        "/api/query?start=1h-ago&m=sum:sys.cpu.user");
    rpc.execute(tsdb, query);
    assertEquals(HttpResponseStatus.BAD_REQUEST, query.response().getStatus());
    final String json =
        query.response().getContent().toString(Charset.forName("UTF-8"));
    assertTrue(json.contains("No such name for 'foo': 'metrics'"));
  }
}
