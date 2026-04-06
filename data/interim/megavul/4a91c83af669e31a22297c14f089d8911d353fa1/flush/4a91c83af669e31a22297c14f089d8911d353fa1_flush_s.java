class flush {
@Override
    public void flush() {
        synchronized (builder) {
            try {
                body = new String(builder.toString().getBytes(StandardCharsets.UTF_8));
                boolean includeEndTag = isIncludePageEndTag(body);
                if (includeEndTag && response.getContentType().contains("text/html")) {
                    body = getCompressAndParseHtml(body);
                }
                out.write(body);

                // Reset the local StringBuilder and issue real flush.
                builder.setLength(0);
                super.flush();
            } catch (IOException ex) {
                LOGGER.error("", ex);
            }
        }
    }
}
