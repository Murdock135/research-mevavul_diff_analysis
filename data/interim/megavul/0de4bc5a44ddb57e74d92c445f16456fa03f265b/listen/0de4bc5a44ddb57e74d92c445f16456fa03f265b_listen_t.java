class listen {
public StompServer listen(int port, String host, Handler<AsyncResult<StompServer>> handler) {
    if (port == -1) {
      handler.handle(Future.failedFuture("TCP server disabled. The port is set to '-1'."));
      return this;
    }
    StompServerHandler stomp;
    synchronized (this) {
      stomp = this.handler;
    }

    Objects.requireNonNull(stomp, "Cannot open STOMP server - no StompServerConnectionHandler attached to the " +
        "server.");
    server
        .connectHandler(socket -> {
          AtomicBoolean connected = new AtomicBoolean();
          AtomicBoolean firstFrame = new AtomicBoolean();
          StompServerConnection connection = new StompServerTCPConnectionImpl(socket, this, frame -> {
            if (frame.frame().getCommand() == Command.CONNECTED) {
              connected.set(true);
            }
            Handler<ServerFrame> h = writingFrameHandler;
            if (h != null) {
              h.handle(frame);
            }
          });
          FrameParser parser = new FrameParser(options);
          socket.exceptionHandler((exception) -> {
            LOGGER.error("The STOMP server caught a TCP socket error - closing connection", exception);
            connection.close();
          });
          socket.endHandler(v -> connection.close());
          parser
              .errorHandler((exception) -> {
                    connection.write(
                        Frames.createInvalidFrameErrorFrame(exception));
                    connection.close();
                  }
              )
              .handler(frame -> {
                if (frame.getCommand() == Command.CONNECT || frame.getCommand() == Command.STOMP) {
                  if (firstFrame.compareAndSet(false, true)) {
                    stomp.handle(new ServerFrameImpl(frame, connection));
                  } else {
                    connection.write(Frames.createErrorFrame("Already connected", Collections.emptyMap(), ""));
                    connection.close();
                  }
                } else if (connected.get()) {
                  stomp.handle(new ServerFrameImpl(frame, connection));
                } else {
                  connection.write(Frames.createErrorFrame("Not connected", Collections.emptyMap(), ""));
                  connection.close();
                }
              });
          socket.handler(parser);
        })
        .listen(port, host).onComplete(ar -> {
          if (ar.failed()) {
            if (handler != null) {
              vertx.runOnContext(v -> handler.handle(Future.failedFuture(ar.cause())));
            } else {
              LOGGER.error(ar.cause());
            }
          } else {
            listening = true;
            LOGGER.info("STOMP server listening on " + ar.result().actualPort());
            if (handler != null) {
              vertx.runOnContext(v -> handler.handle(Future.succeededFuture(this)));
            }
          }
        });
    return this;
  }
}
