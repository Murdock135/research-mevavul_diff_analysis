class setPlotParams {
static void setPlotParams(final HttpQuery query, final Plot plot) {
    final HashMap<String, String> params = new HashMap<String, String>();
    final Map<String, List<String>> querystring = query.getQueryString();
    String value;
    if ((value = popParam(querystring, "yrange")) != null) {
      validateString("yrange", value, "[:]");
      if (!RANGE_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'yrange' was invalid. "
            + "Must be in the format [min:max].");
      }
      params.put("yrange", value);
    }
    if ((value = popParam(querystring, "y2range")) != null) {
      validateString("y2range", value, "[:]");
      if (!RANGE_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'y2range' was invalid. "
            + "Must be in the format [min:max].");
      }
      params.put("y2range", value);
    }
    if ((value = popParam(querystring, "ylabel")) != null) {
      validateString("ylabel", value, " ");
      if (!LABEL_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'ylabel' was invalid. Must "
            + "satisfy the pattern " + LABEL_VALIDATOR.toString());
      }
      params.put("ylabel", stringify(value));
    }
    if ((value = popParam(querystring, "y2label")) != null) {
      validateString("y2label", value, " ");
      if (!LABEL_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'y2label' was invalid. Must "
            + "satisfy the pattern " + LABEL_VALIDATOR.toString());
      }
      params.put("y2label", stringify(value));
    }
    if ((value = popParam(querystring, "yformat")) != null) {
      validateString("yformat", value, "% ");
      if (!FORMAT_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'yformat' was invalid. Must "
            + "satisfy the pattern " + FORMAT_VALIDATOR.toString());
      }
      params.put("format y", stringify(value));
    }
    if ((value = popParam(querystring, "y2format")) != null) {
      validateString("y2format", value, "% ");
      if (!FORMAT_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'y2format' was invalid. Must "
            + "satisfy the pattern " + FORMAT_VALIDATOR.toString());
      }
      params.put("format y2", stringify(value));
    }
    if ((value = popParam(querystring, "xformat")) != null) {
      validateString("xformat", value, "% ");
      if (!FORMAT_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'xformat' was invalid. Must "
            + "satisfy the pattern " + FORMAT_VALIDATOR.toString());
      }
      params.put("format x", stringify(value));
    }
    if ((value = popParam(querystring, "ylog")) != null) {
      params.put("logscale y", "");
    }
    if ((value = popParam(querystring, "y2log")) != null) {
      params.put("logscale y2", "");
    }
    if ((value = popParam(querystring, "key")) != null) {
      validateString("key", value);
      if (!KEY_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'key' was invalid. Must "
            + "satisfy the pattern " + KEY_VALIDATOR.toString());
      }
      params.put("key", value);
    }
    if ((value = popParam(querystring, "title")) != null) {
      validateString("title", value, " ");
      if (!LABEL_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'title' was invalid. Must "
            + "satisfy the pattern " + LABEL_VALIDATOR.toString());
      }
      params.put("title", stringify(value));
    }
    if ((value = popParam(querystring, "bgcolor")) != null) {
      validateString("bgcolor", value);
      if (!COLOR_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'bgcolor' was invalid. Must "
            + "be a hex value e.g. 'xFFFFFF'");
      }
      params.put("bgcolor", value);
    }
    if ((value = popParam(querystring, "fgcolor")) != null) {
      validateString("fgcolor", value);
      if (!COLOR_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'fgcolor' was invalid. Must "
            + "be a hex value e.g. 'xFFFFFF'");
      }
      params.put("fgcolor", value);
    }
    if ((value = popParam(querystring, "smooth")) != null) {
      validateString("smooth", value);
      if (!SMOOTH_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'smooth' was invalid. Must "
            + "satisfy the pattern " + SMOOTH_VALIDATOR.toString());
      }
      params.put("smooth", value);
    }
    if ((value = popParam(querystring, "style")) != null) {
      validateString("style", value);
      if (!STYLE_VALIDATOR.matcher(value).find()) {
        throw new BadRequestException("'style' was invalid. Must "
            + "satisfy the pattern " + STYLE_VALIDATOR.toString());
      }
      params.put("style", value);
    }
    // This must remain after the previous `if' in order to properly override
    // any previous `key' parameter if a `nokey' parameter is given.
    if ((value = popParam(querystring, "nokey")) != null) {
      params.put("key", null);
    }
    plot.setParams(params);
  }
}
