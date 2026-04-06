class consumeHeaderFrame {
private void consumeHeaderFrame(Frame f) throws IOException {
        if (f.type == AMQP.FRAME_HEADER) {
            this.contentHeader = AMQImpl.readContentHeaderFrom(f.getInputStream());
            this.remainingBodyBytes = this.contentHeader.getBodySize();
            updateContentBodyState();
        } else {
            throw new UnexpectedFrameError(f, AMQP.FRAME_HEADER);
        }
    }
}
