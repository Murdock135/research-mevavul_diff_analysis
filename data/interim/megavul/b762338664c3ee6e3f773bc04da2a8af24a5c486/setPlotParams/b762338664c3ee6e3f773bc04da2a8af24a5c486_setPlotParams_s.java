class setPlotParams {
static void setPlotParams(final HttpQuery query, final Plot plot) {
    final HashMap<String, String> params = new HashMap<String, String>();
    final Map<String, List<String>> querystring = query.getQueryString();
    String value;
    if ((value = popParam(querystring, "yrange")) != null) {
      params.put("yrange", value);
    }
    if ((value = popParam(querystring, "y2range")) != null) {
      params.put("y2range", value);
    }
    if ((value = popParam(querystring, "ylabel")) != null) {
      params.put("ylabel", stringify(value));
    }
    if ((value = popParam(querystring, "y2label")) != null) {
      params.put("y2label", stringify(value));
    }
    if ((value = popParam(querystring, "yformat")) != null) {
      params.put("format y", stringify(value));
    }
    if ((value = popParam(querystring, "y2format")) != null) {
      params.put("format y2", stringify(value));
    }
    if ((value = popParam(querystring, "xformat")) != null) {
      params.put("format x", stringify(value));
    }
    if ((value = popParam(querystring, "ylog")) != null) {
      params.put("logscale y", "");
    }
    if ((value = popParam(querystring, "y2log")) != null) {
      params.put("logscale y2", "");
    }
    if ((value = popParam(querystring, "key")) != null) {
      params.put("key", value);
    }
    if ((value = popParam(querystring, "title")) != null) {
      params.put("title", stringify(value));
    }
    if ((value = popParam(querystring, "bgcolor")) != null) {
      params.put("bgcolor", value);
    }
    if ((value = popParam(querystring, "fgcolor")) != null) {
      params.put("fgcolor", value);
    }
    if ((value = popParam(querystring, "smooth")) != null) {
      params.put("smooth", value);
    }
    if ((value = popParam(querystring, "style")) != null) {
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
