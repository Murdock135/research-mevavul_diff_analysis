class webSocketHandler {
@Override
  public Handler<ServerWebSocket> webSocketHandler() {
    if (!options.isWebsocketBridge()) {
      return null;
    }

    StompServerHandler stomp;
    synchronized (this) {
      stomp = this.handler;
    }

    return socket -> {
      if (!socket.path().equals(options.getWebsocketPath())) {
        LOGGER.error("Receiving a web socket connection on an invalid path (" + socket.path() + "), the path is " +
            "configured to " + options.getWebsocketPath() + ". Rejecting connection");
        socket.reject();
        return;
      }
      AtomicBoolean connected = new AtomicBoolean();
      AtomicBoolean firstFrame = new AtomicBoolean();
      StompServerConnection connection = new StompServerWebSocketConnectionImpl(socket, this, frame -> {
        if (frame.frame().getCommand() == Command.CONNECTED  || frame.frame().getCommand() == Command.STOMP) {
          connected.set(true);
        }
        Handler<ServerFrame> h = writingFrameHandler;
        if (h != null) {
          h.handle(frame);
        }
      });
      FrameParser parser = new FrameParser(options);
      socket.exceptionHandler((exception) -> {
        LOGGER.error("The STOMP server caught a WebSocket error - closing connection", exception);
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
            if (frame.getCommand() == Command.CONNECT) {
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
    };
  }
}
