class sendAllJSON {
private void sendAllJSON(HttpExchange pExchange, ParsedUri pParsedUri, JSONAware pJson) throws IOException {
        OutputStream out = null;
        try {
            Headers headers = pExchange.getResponseHeaders();
            if (pJson != null) {
                headers.set("Content-Type", getMimeType(pParsedUri) + "; charset=utf-8");
                String json = pJson.toJSONString();
                String callback = pParsedUri.getParameter(ConfigKey.CALLBACK.getKeyValue());
                String content = callback != null && MimeTypeUtil.isValidCallback(callback) ? callback + "(" + json + ");" : json;
                byte[] response = content.getBytes("UTF8");
                pExchange.sendResponseHeaders(200,response.length);
                out = pExchange.getResponseBody();
                out.write(response);
            } else {
                headers.set("Content-Type", "text/plain");
                pExchange.sendResponseHeaders(200,-1);
            }
        } finally {
            if (out != null) {
                // Always close in order to finish the request.
                // Otherwise the thread blocks.
                out.close();
            }
        }
    }
}
