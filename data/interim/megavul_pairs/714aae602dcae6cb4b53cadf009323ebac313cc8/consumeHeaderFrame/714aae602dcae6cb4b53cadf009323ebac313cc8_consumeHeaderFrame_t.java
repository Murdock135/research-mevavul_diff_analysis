class consumeHeaderFrame {
private void consumeHeaderFrame(Frame f) throws IOException {
        if (f.type == AMQP.FRAME_HEADER) {
            this.contentHeader = AMQImpl.readContentHeaderFrom(f.getInputStream());
            long bodySize = this.contentHeader.getBodySize();
            if (bodySize >= this.maxBodyLength) {
                throw new IllegalStateException(format(
                    "Message body is too large (%d), maximum size is %d",
                    bodySize, this.maxBodyLength
                ));
            }
            this.remainingBodyBytes = bodySize;
            updateContentBodyState();
        } else {
            throw new UnexpectedFrameError(f, AMQP.FRAME_HEADER);
        }
    }
}
