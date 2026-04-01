class notFound {
public static RouteWithFilter notFound(final String method, final String path) {
    return new FallbackRoute("404", method, path, MediaType.ALL, (req, rsp, chain) -> {
      if (!rsp.status().isPresent()) {
        throw new Err(Status.NOT_FOUND, req.path());
      }
    });
  }
}
