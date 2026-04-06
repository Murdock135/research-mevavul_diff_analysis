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
      StompServerConnection connection = new StompServerWebSocketConnectionImpl(socket, this, writingFrameHandler);
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
          .handler(frame -> stomp.handle(new ServerFrameImpl(frame, connection)));
      socket.handler(parser);
    };
  }
}
