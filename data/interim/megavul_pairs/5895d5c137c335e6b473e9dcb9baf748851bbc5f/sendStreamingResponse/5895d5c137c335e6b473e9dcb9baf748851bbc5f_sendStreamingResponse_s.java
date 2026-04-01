class sendStreamingResponse {
private void sendStreamingResponse(HttpExchange pExchange, ParsedUri pParsedUri, JSONStreamAware pJson) throws IOException {
        Headers headers = pExchange.getResponseHeaders();
        if (pJson != null) {
            headers.set("Content-Type", getMimeType(pParsedUri) + "; charset=utf-8");
            String callback = pParsedUri.getParameter(ConfigKey.CALLBACK.getKeyValue());
            pExchange.sendResponseHeaders(200, 0);
            Writer writer = new OutputStreamWriter(pExchange.getResponseBody(), "UTF-8");
            IoUtil.streamResponseAndClose(writer, pJson, callback);
        } else {
            headers.set("Content-Type", "text/plain");
            pExchange.sendResponseHeaders(200,-1);
        }
    }
}
