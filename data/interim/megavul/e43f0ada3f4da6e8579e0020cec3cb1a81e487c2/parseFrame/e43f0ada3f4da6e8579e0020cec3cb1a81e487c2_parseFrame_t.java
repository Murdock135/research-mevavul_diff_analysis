class parseFrame {
@Override
    protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
        Http2FrameHeaderParser frameParser;
        do {
            frameParser = parseFrameNoContinuation(data);
            // if the frame requires continuation and there is remaining data in the buffer
            // it should be consumed cos spec ensures the next frame is the continuation
        } while(frameParser != null && frameParser.getContinuationParser() != null && data.hasRemaining());
        return frameParser;
    }
}
