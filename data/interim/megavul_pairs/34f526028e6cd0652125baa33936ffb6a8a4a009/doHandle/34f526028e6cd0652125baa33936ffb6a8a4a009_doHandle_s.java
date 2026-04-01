class doHandle {
private void doHandle(final Request req, final Response rsp, final Asset asset) throws Throwable {

    // handle etag
    if (this.etag) {
      String etag = asset.etag();
      boolean ifnm = req.header("If-None-Match").toOptional()
          .map(etag::equals)
          .orElse(false);
      if (ifnm) {
        rsp.header("ETag", etag).status(Status.NOT_MODIFIED).end();
        return;
      }

      rsp.header("ETag", etag);
    }

    // Handle if modified since
    if (this.lastModified) {
      long lastModified = asset.lastModified();
      if (lastModified > 0) {
        boolean ifm = req.header("If-Modified-Since").toOptional(Long.class)
            .map(ifModified -> lastModified / 1000 <= ifModified / 1000)
            .orElse(false);
        if (ifm) {
          rsp.status(Status.NOT_MODIFIED).end();
          return;
        }
        rsp.header("Last-Modified", new Date(lastModified));
      }
    }

    // cache max-age
    if (maxAge > 0) {
      rsp.header("Cache-Control", "max-age=" + maxAge);
    }

    send(req, rsp, asset);
  }
}
