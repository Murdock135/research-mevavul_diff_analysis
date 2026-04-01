class write_1 {
public long write(final InputStream inputStream, final RASegmentOutputStream segmentOutputStream,
                      final boolean close) {
        long bytesWritten = 0;
        try {
            try {
                bytesWritten = StreamUtil.streamToStream(inputStream, segmentOutputStream, close);
            } finally {
                try {
                    // Ensure all streams are closed.
                    if (segmentOutputStream != null) {
                        segmentOutputStream.flush();
                        if (close) {
                            segmentOutputStream.close();
                        }
                    }

                } finally {
                    if (close && inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            return bytesWritten;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
