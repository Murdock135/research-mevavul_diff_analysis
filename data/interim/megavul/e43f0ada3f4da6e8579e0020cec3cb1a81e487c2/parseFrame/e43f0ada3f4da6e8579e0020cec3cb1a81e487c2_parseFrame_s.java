class parseFrame {
@Override
    protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
        if (prefaceCount < PREFACE_BYTES.length) {
            while (data.hasRemaining() && prefaceCount < PREFACE_BYTES.length) {
                if (data.get() != PREFACE_BYTES[prefaceCount]) {
                    IoUtils.safeClose(getUnderlyingConnection());
                    throw UndertowMessages.MESSAGES.incorrectHttp2Preface();
                }
                prefaceCount++;
            }
        }
        Http2FrameHeaderParser frameParser = this.frameParser;
        if (frameParser == null) {
            this.frameParser = frameParser = new Http2FrameHeaderParser(this, continuationParser);
            this.continuationParser = null;
        }
        if (!frameParser.handle(data)) {
            return null;
        }
        if (!initialSettingsReceived) {
            if (frameParser.type != FRAME_TYPE_SETTINGS) {
                UndertowLogger.REQUEST_IO_LOGGER.remoteEndpointFailedToSendInitialSettings(frameParser.type);
                //StringBuilder sb = new StringBuilder();
                //while (data.hasRemaining()) {
                //    sb.append((char)data.get());
                //    sb.append(" ");
                //}
                markReadsBroken(new IOException());
            } else {
                initialSettingsReceived = true;
            }
        }
        this.frameParser = null;
        if (frameParser.getActualLength() > receiveMaxFrameSize && frameParser.getActualLength() > unackedReceiveMaxFrameSize) {
            sendGoAway(ERROR_FRAME_SIZE_ERROR);
            throw UndertowMessages.MESSAGES.http2FrameTooLarge();
        }
        if (frameParser.getContinuationParser() != null) {
            this.continuationParser = frameParser.getContinuationParser();
            return null;
        }
        return frameParser;

    }
}
