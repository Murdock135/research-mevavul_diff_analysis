class setParams {
public void setParams(final Map<String, String> params) {
    // check "format y" and "format y2"
    String[] y_format_keys = {"format y", "format y2"};
    for(String k : y_format_keys){
      if(params.containsKey(k)){
        params.put(k, URLDecoder.decode(params.get(k)));
      }
    }
    this.params = params;
  }
}
