class readFrame {
@Override
    public Frame readFrame() throws IOException {
        synchronized (_inputStream) {
            return Frame.readFrom(_inputStream, this.maxInboundMessageBodySize);
        }
    }
}
