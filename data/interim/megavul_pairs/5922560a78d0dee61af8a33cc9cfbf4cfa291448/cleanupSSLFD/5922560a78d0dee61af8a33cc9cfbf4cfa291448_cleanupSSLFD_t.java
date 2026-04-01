class cleanupSSLFD {
private void cleanupSSLFD() {
        if (!closed_fd && ssl_fd != null) {
            try {
                SSL.RemoveCallbacks(ssl_fd);
                ssl_fd.close();
                ssl_fd = null;
            } catch (Exception e) {
                logger.error("Got exception trying to cleanup SSLFD", e);
            } finally {
                closed_fd = true;
            }
        }

        if (read_buf != null) {
            Buffer.Free(read_buf);
            read_buf = null;
        }

        if (write_buf != null) {
            Buffer.Free(write_buf);
            write_buf = null;
        }
    }
}
