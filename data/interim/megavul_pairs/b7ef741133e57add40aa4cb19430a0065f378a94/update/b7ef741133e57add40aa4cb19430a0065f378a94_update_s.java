class update {
public void update(String table, UpdateSection section, Criterion when, boolean returnUpdatedIdsCount,
      Handler<AsyncResult<UpdateResult>> replyHandler) {
    long start = System.nanoTime();
    vertx.runOnContext(v -> {
      client.getConnection(res -> {
        if (res.succeeded()) {
          SQLConnection connection = res.result();
          StringBuilder sb = new StringBuilder();
          if (when != null) {
            sb.append(when.toString());
          }
          StringBuilder returning = new StringBuilder();
          if (returnUpdatedIdsCount) {
            returning.append(RETURNING_ID);
          }
          try {
            String q = UPDATE + schemaName + DOT + table + SET + DEFAULT_JSONB_FIELD_NAME + " = jsonb_set(" + DEFAULT_JSONB_FIELD_NAME + ","
                + section.getFieldsString() + ", '" + section.getValue() + "', false) " + sb.toString() + SPACE + returning;
            log.debug("update query = " + q);
            connection.update(q, query -> {
              connection.close();
              if (query.failed()) {
                log.error(query.cause().getMessage(), query.cause());
                replyHandler.handle(Future.failedFuture(query.cause()));
              } else {
                replyHandler.handle(Future.succeededFuture(query.result()));
              }
              statsTracker(UPDATE_STAT_METHOD, table, start);
            });
          } catch (Exception e) {
            if(connection != null){
              connection.close();
            }
            log.error(e.getMessage(), e);
            replyHandler.handle(Future.failedFuture(e));
          }
        } else {
          log.error(res.cause().getMessage(), res.cause());
          replyHandler.handle(Future.failedFuture(res.cause()));
        }
      });
    });
  }
}
