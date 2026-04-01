class routes {
private static Route[] routes(final Set<Route.Definition> routeDefs, final String method,
      final String path, final MediaType type, final List<MediaType> accept) {
    List<Route> routes = findRoutes(routeDefs, method, path, type, accept);

    routes.add(RouteImpl.fallback((req, rsp, chain) -> {
      if (!rsp.status().isPresent()) {
        // 406 or 415
        Err ex = handle406or415(routeDefs, method, path, type, accept);
        if (ex != null) {
          throw ex;
        }
        // 405
        ex = handle405(routeDefs, method, path, type, accept);
        if (ex != null) {
          throw ex;
        }
        // favicon.ico
        if (path.equals("/favicon.ico")) {
          // default /favicon.ico handler:
          rsp.status(Status.NOT_FOUND).end();
        } else {
          throw new Err(Status.NOT_FOUND, req.path());
        }
      }
    }, method, path, "err", accept));

    return routes.toArray(new Route[routes.size()]);
  }
}
