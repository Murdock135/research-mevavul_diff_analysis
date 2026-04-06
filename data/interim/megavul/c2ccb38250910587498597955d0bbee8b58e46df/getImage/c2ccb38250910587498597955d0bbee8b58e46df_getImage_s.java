class getImage {
private byte[] getImage(String url) {
        // Try to get the fiveicon from the url using an HTTP connection from the pool
        // that also allows to configure timeout values (e.g. connect and get data)
        final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(2000)
            .setSocketTimeout(2000)
            .build();
        final HttpUriRequest getRequest = RequestBuilder.get(url)
            .setConfig(requestConfig)
            .build();

        try(final CloseableHttpResponse response = client.execute(getRequest)) {
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toByteArray(response.getEntity());
            }
        } catch (final IOException ignored) {
            // Do nothing
        }

        return null;
    }
}
