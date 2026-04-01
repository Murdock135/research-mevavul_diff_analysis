class readFrame_1 {
public Frame readFrame() throws IOException {
        while (somethingToRead()) {
            if (bytesRead == 0) {
                // type
                frameType = readFromBuffer();
                if (frameType == 'A') {
                    handleProtocolVersionMismatch();
                }
            } else if (bytesRead == 1) {
                // channel 1/2
                frameBuffer[0] = readFromBuffer();
            } else if (bytesRead == 2) {
                // channel 2/2
                frameChannel = (frameBuffer[0] << 8) + readFromBuffer();
            } else if (bytesRead == 3) {
                // payload size 1/4
                frameBuffer[0] = readFromBuffer();
            } else if (bytesRead == 4) {
                // payload size 2/4
                frameBuffer[1] = readFromBuffer();
            } else if (bytesRead == 5) {
                // payload size 3/4
                frameBuffer[2] = readFromBuffer();
            } else if (bytesRead == 6) {
                // payload size 4/4
                int framePayloadSize = (frameBuffer[0] << 24) + (frameBuffer[1] << 16) + (frameBuffer[2] << 8) + readFromBuffer();
                framePayload = new byte[framePayloadSize];
            } else if (bytesRead >= PAYLOAD_OFFSET && bytesRead < framePayload.length + PAYLOAD_OFFSET) {
                framePayload[bytesRead - PAYLOAD_OFFSET] = (byte) readFromBuffer();
            } else if (bytesRead == framePayload.length + PAYLOAD_OFFSET) {
                int frameEndMarker = readFromBuffer();
                if (frameEndMarker != AMQP.FRAME_END) {
                    throw new MalformedFrameException("Bad frame end marker: " + frameEndMarker);
                }
                bytesRead = 0;
                return new Frame(frameType, frameChannel, framePayload);
            } else {
                throw new IllegalStateException("Number of read bytes incorrect: " + bytesRead);
            }
            bytesRead++;
        }
        return null;
    }
}
