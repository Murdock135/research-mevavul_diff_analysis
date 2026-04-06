class handleErrorResponse {
private static void handleErrorResponse(HttpsURLConnection conn, String url, String moduleFullName) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), Charset.defaultCharset()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            MapValue payload = (MapValue) JSONParser.parse(result.toString());
            createError("error: " + payload.getStringValue("message"));
        } catch (IOException e) {
            createError("failed to pull the module '" + moduleFullName + "' from the remote repository '" + url + "'");
        }
    }
}
