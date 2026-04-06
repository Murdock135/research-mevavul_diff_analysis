class buildFromCodec {
private T buildFromCodec(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder) {
        int maxConsecutiveEmptyDataFrames = decoderEnforceMaxConsecutiveEmptyDataFrames();
        if (maxConsecutiveEmptyDataFrames > 0) {
            decoder = new Http2EmptyDataFrameConnectionDecoder(decoder, maxConsecutiveEmptyDataFrames);
        }
        if (maxRstFramesPerWindow > 0 && secondsPerWindow > 0) {
            decoder = new Http2MaxRstFrameDecoder(decoder, maxRstFramesPerWindow, secondsPerWindow);
        }
        final T handler;
        try {
            // Call the abstract build method
            handler = build(decoder, encoder, initialSettings);
        } catch (Throwable t) {
            encoder.close();
            decoder.close();
            throw new IllegalStateException("failed to build an Http2ConnectionHandler", t);
        }

        // Setup post build options
        handler.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
        if (handler.decoder().frameListener() == null) {
            handler.decoder().frameListener(frameListener);
        }
        return handler;
    }
}
