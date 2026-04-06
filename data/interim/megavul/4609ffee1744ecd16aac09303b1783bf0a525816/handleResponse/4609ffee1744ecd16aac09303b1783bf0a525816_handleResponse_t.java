class handleResponse {
private static void handleResponse(HttpsURLConnection conn, String orgName, String moduleName, String version) {
        try {
            int statusCode = getStatusCode(conn);
            // 200 - Module pushed successfully
            // Other - Error occurred, json returned with the error message
            if (statusCode == HttpsURLConnection.HTTP_OK) {
                outStream.println(orgName + "/" + moduleName + ":" + version + " pushed to central successfully");
            } else if (statusCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                errStream.println("unauthorized access token for organization: " + orgName);
            } else if (statusCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), Charset.defaultCharset()))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    MapValue payload = (MapValue) JSONParser.parse(result.toString());
                    String message = payload.getStringValue("message");
                    if (message.contains("module md file cannot be empty")) {
                        errStream.println(message);
                    } else {
                        throw ErrorUtil.createCommandException(message);
                    }
                } catch (IOException e) {
                    throw ErrorUtil.createCommandException(
                            "failed to push the module '" + orgName + "/" + moduleName + ":" + version
                                    + "' to the remote repository '" + conn.getURL() + "'");
                }
            } else {
                throw ErrorUtil.createCommandException(
                        "failed to push the module '" + orgName + "/" + moduleName + ":" + version
                                + "' to the remote repository '" + conn.getURL() + "'");
            }
        } finally {
            conn.disconnect();
        }
    }
}
