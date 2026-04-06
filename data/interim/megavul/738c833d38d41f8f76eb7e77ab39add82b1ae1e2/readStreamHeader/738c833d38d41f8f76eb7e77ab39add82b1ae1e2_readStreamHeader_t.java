class readStreamHeader {
protected void readStreamHeader() throws IOException, StreamCorruptedException {
        if (input.readShort() == STREAM_MAGIC
                && input.readShort() == STREAM_VERSION) {
            return;
        }
        throw new StreamCorruptedException();
    }
}
