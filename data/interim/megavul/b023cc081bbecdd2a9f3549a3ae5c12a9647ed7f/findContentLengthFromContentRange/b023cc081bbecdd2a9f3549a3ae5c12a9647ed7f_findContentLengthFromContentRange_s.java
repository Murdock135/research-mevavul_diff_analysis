class findContentLengthFromContentRange {
public static long findContentLengthFromContentRange(FileDownloadConnection connection) {
        final String contentRange = getContentRangeHeader(connection);
        long contentLength = parseContentLengthFromContentRange(contentRange);
        if (contentLength  < 0) contentLength = TOTAL_VALUE_IN_CHUNKED_RESOURCE;
        return contentLength;
    }
}
