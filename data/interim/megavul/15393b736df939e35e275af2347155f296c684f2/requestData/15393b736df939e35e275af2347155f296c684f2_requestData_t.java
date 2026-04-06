class requestData {
protected Map<String, String> requestData(Http.Request request) {

    Map<String, String[]> urlFormEncoded = new HashMap<>();
    if (request.body().asFormUrlEncoded() != null) {
      urlFormEncoded = request.body().asFormUrlEncoded();
    }

    Map<String, String[]> multipartFormData = new HashMap<>();
    if (request.body().asMultipartFormData() != null) {
      multipartFormData = request.body().asMultipartFormData().asFormUrlEncoded();
    }

    Map<String, String> jsonData = new HashMap<>();
    if (request.body().asJson() != null) {
      jsonData =
          play.libs.Scala.asJava(
              play.api.data.FormUtils.fromJson(
                  play.api.libs.json.Json.parse(play.libs.Json.stringify(request.body().asJson())),
                  maxJsonChars(),
                  maxJsonDepth()));
    }

    Map<String, String> data = new HashMap<>();

    fillDataWith(data, urlFormEncoded);
    fillDataWith(data, multipartFormData);

    jsonData.forEach(data::put);

    if (!request.method().equalsIgnoreCase(HttpVerbs.POST)
        && !request.method().equalsIgnoreCase(HttpVerbs.PUT)
        && !request.method().equalsIgnoreCase(HttpVerbs.PATCH)) {
      fillDataWith(data, request.queryString());
    }

    return data;
  }
}
