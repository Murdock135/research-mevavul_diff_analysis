class savePresentation {
public boolean savePresentation(final String meetingId,
            final String filename, final String urlString) {

        String finalUrl = followRedirect(meetingId, urlString, 0, urlString);

        if (finalUrl == null) return false;

        boolean success = false;

        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        try {
            httpclient.start();
            File download = new File(filename);
            ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(download) {
                @Override
                protected File process(
                        final HttpResponse response,
                        final File file,
                        final ContentType contentType) throws Exception {
                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        throw new ClientProtocolException("Upload failed: " + response.getStatusLine());
                    }
                    return file;
                }

            };
            Future<File> future = httpclient.execute(HttpAsyncMethods.createGet(finalUrl), consumer, null);
            File result = future.get();
            success = result.exists();
        } catch (java.lang.InterruptedException ex) {
            log.error("InterruptedException while saving presentation", meetingId, ex);
        } catch (java.util.concurrent.ExecutionException ex) {
            log.error("ExecutionException while saving presentation", meetingId, ex);
        } catch (java.io.FileNotFoundException ex) {
            log.error("FileNotFoundException while saving presentation", meetingId, ex);
        } finally {
            try {
                httpclient.close();
            } catch (java.io.IOException ex) {
                log.error("IOException while saving presentation", meetingId, ex);
            }
        }

        return success;
    }
}
