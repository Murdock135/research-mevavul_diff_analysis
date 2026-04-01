class convert_1 {
private String convert(URL url) throws IOException, URISyntaxException
    {
        HttpEntity entity = fetch(url.toURI());
        // Remove the content type parameters, such as the charset, so they don't influence the diff.
        String contentType = StringUtils.substringBefore(entity.getContentType().getValue(), ";");
        byte[] content = IOUtils.toByteArray(entity.getContent());
        return String.format("data:%s;base64,%s", contentType, Base64.getEncoder().encodeToString(content));
    }
}
