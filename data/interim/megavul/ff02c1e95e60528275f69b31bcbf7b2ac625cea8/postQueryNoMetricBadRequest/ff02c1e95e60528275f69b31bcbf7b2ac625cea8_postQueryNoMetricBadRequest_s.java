class postQueryNoMetricBadRequest {
@Test
  public void postQueryNoMetricBadRequest() throws Exception {
    final DeferredGroupException dge = mock(DeferredGroupException.class);
    when(dge.getCause()).thenReturn(new NoSuchUniqueName("foo", "metrics"));

    when(query_result.configureFromQuery((TSQuery)any(), anyInt()))
      .thenReturn(Deferred.fromError(dge));

    HttpQuery query = NettyMocks.postQuery(tsdb, "/api/query",
        "{\"start\":1425440315306,\"queries\":" +
          "[{\"metric\":\"nonexistent\",\"aggregator\":\"sum\",\"rate\":true," +
          "\"rateOptions\":{\"counter\":false}}]}");
    rpc.execute(tsdb, query);
    assertEquals(HttpResponseStatus.BAD_REQUEST, query.response().getStatus());
    final String json =
        query.response().getContent().toString(Charset.forName("UTF-8"));
    assertTrue(json.contains("No such name for 'foo': 'metrics'"));
  }
}
