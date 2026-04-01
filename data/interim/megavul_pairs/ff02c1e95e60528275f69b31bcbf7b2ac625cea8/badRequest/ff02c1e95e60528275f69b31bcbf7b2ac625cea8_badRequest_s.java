class badRequest {
@Override
  public void badRequest(final BadRequestException exception) {
    logWarn("Bad Request on " + request().getUri() + ": " + exception.getMessage());
    if (this.api_version > 0) {
      // always default to the latest version of the error formatter since we
      // need to return something
      switch (this.api_version) {
        case 1:
        default:
          sendReply(exception.getStatus(), serializer.formatErrorV1(exception));
      }
      return;
    }
    if (hasQueryStringParam("json")) {
      final StringBuilder buf = new StringBuilder(10 +
          exception.getDetails().length());
      buf.append("{\"err\":\"");
      HttpQuery.escapeJson(exception.getMessage(), buf);
      buf.append("\"}");
      sendReply(HttpResponseStatus.BAD_REQUEST, buf);
    } else {
      sendReply(HttpResponseStatus.BAD_REQUEST,
                makePage("Bad Request", "Looks like it's your fault this time",
                         "<blockquote>"
                         + "<h1>Bad Request</h1>"
                         + "Sorry but your request was rejected as being"
                         + " invalid.<br/><br/>"
                         + "The reason provided was:<blockquote>"
                         + exception.getMessage()
                         + "</blockquote></blockquote>"));
    }
  }
}
