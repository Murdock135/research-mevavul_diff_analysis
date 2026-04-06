class doDownload {
protected byte[] doDownload(String url) {
        if (DEBUG) Log.d(TAG, "Downloading XTRA data from " + url);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestProperty(
                    "Accept",
                    "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic");
            connection.setRequestProperty(
                    "x-wap-profile",
                    "http://www.openmobilealliance.org/tech/profiles/UAPROF/ccppschema-20021212#");
            connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);

            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                if (DEBUG) Log.d(TAG, "HTTP error downloading gps XTRA: " + statusCode);
                return null;
            }

            try (InputStream in = connection.getInputStream()) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = in.read(buffer)) != -1) {
                    bytes.write(buffer, 0, count);
                    if (bytes.size() > MAXIMUM_CONTENT_LENGTH_BYTES) {
                        if (DEBUG) Log.d(TAG, "XTRA file too large");
                        return null;
                    }
                }
                return bytes.toByteArray();
            }
        } catch (IOException ioe) {
            if (DEBUG) Log.d(TAG, "Error downloading gps XTRA: ", ioe);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
