class popParam {
private static String popParam(final Map<String, List<String>> querystring,
                                     final String param) {
    final List<String> params = querystring.remove(param);
    if (params == null) {
      return null;
    }
    final String given = params.get(params.size() - 1);
    // TODO - far from perfect, should help a little.
    if (given.contains("`") || given.contains("%60") || 
        given.contains("&#96;")) {
      throw new BadRequestException("Parameter " + param + " contained a "
          + "back-tick. That's a no-no.");
    }
    return given;
  }
}
