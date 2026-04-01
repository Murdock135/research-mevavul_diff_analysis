class pathOffset {
public static String pathOffset(String path, RoutingContext context) {
    final Route route = context.currentRoute();

    // cannot make any assumptions
    if (route == null) {
      return path;
    }

    if (!route.isExactPath()) {
      final String rest = context.pathParam("*");
      if (rest != null) {
        // normalize
        if (rest.length() > 0) {
          if (rest.charAt(0) == '/') {
            return rest;
          } else {
            return "/" + rest;
          }
        } else {
          return "/";
        }
      }
    }
    int prefixLen = 0;
    String mountPoint = context.mountPoint();
    if (mountPoint != null) {
      prefixLen = mountPoint.length();
      // special case we need to verify if a trailing slash  is present and exclude
      if (mountPoint.charAt(mountPoint.length() - 1) == '/') {
        prefixLen--;
      }
    }
    // we can only safely skip the route path if there are no variables or regex
    if (!route.isRegexPath()) {
      String routePath = route.getPath();
      if (routePath != null) {
        prefixLen += routePath.length();
        // special case we need to verify if a trailing slash  is present and exclude
        if (routePath.charAt(routePath.length() - 1) == '/') {
          prefixLen--;
        }
      }
    }
    return prefixLen != 0 ? path.substring(prefixLen) : path;
  }
}
