class getResponse {
private XOPMultipartProxyGetFileResponse getResponse(String content) throws Exception {
        XOPMultipartProxyGetFileResponse response = new XOPMultipartProxyGetFileResponse();
        File out = File.createTempFile("tmp", ".txt");
        out.deleteOnExit();
        try (FileWriter writer = new FileWriter(out)) {
            writer.write(content);
            DataSource fds = new FileDataSource(out);
            DataHandler handler = new DataHandler(fds);
            response.setData(handler);
        }
        return response;
    }
}
