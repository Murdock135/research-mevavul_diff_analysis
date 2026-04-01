class setPlotDimensions {
static void setPlotDimensions(final HttpQuery query, final Plot plot) {
    String wxh = query.getQueryStringParam("wxh");
    if (wxh != null && !wxh.isEmpty()) {
      wxh = URLDecoder.decode(wxh.trim());
      validateString("wxh", wxh);
      if (!WXH_VALIDATOR.matcher(wxh).find()) {
        throw new IllegalArgumentException("'wxh' was invalid. "
            + "Must satisfy the pattern " + WXH_VALIDATOR.toString());
      }
      final int wxhlength = wxh.length();
      if (wxhlength < 7) {  // 100x100 minimum.
        throw new BadRequestException("Parameter wxh too short: " + wxh);
      }
      final int x = wxh.indexOf('x', 3);  // Start at 2 as min size is 100x100
      if (x < 0) {
        throw new BadRequestException("Invalid wxh parameter: " + wxh);
      }
      try {
        final short width = Short.parseShort(wxh.substring(0, x));
        final short height = Short.parseShort(wxh.substring(x + 1, wxhlength));
        try {
          plot.setDimensions(width, height);
        } catch (IllegalArgumentException e) {
          throw new BadRequestException("Invalid wxh parameter: " + wxh + ", "
                                        + e.getMessage());
        }
      } catch (NumberFormatException e) {
        throw new BadRequestException("Can't parse wxh '" + wxh + "': "
                                      + e.getMessage());
      }
    }
  }
}
