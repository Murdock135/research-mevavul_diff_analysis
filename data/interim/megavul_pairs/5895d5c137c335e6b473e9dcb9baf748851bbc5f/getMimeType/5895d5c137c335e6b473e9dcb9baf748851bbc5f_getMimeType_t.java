class getMimeType {
private String getMimeType(ParsedUri pParsedUri) {
        return MimeTypeUtil.getResponseMimeType(
            pParsedUri.getParameter(ConfigKey.MIME_TYPE.getKeyValue()),
            configuration.get(ConfigKey.MIME_TYPE),
            pParsedUri.getParameter(ConfigKey.CALLBACK.getKeyValue()));
    }
}
