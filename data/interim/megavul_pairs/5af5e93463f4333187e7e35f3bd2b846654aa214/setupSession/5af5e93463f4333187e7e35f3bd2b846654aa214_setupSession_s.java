class setupSession {
OpenSSLSessionImpl setupSession(long sslSessionNativePointer, long sslNativePointer,
            final OpenSSLSessionImpl sessionToReuse, String hostname, int port,
            boolean handshakeCompleted) throws IOException {
        OpenSSLSessionImpl sslSession = null;
        byte[] sessionId = NativeCrypto.SSL_SESSION_session_id(sslSessionNativePointer);
        if (sessionToReuse != null && Arrays.equals(sessionToReuse.getId(), sessionId)) {
            sslSession = sessionToReuse;
            sslSession.lastAccessedTime = System.currentTimeMillis();
            NativeCrypto.SSL_SESSION_free(sslSessionNativePointer);
        } else {
            if (!getEnableSessionCreation()) {
                // Should have been prevented by
                // NativeCrypto.SSL_set_session_creation_enabled
                throw new IllegalStateException("SSL Session may not be created");
            }
            X509Certificate[] localCertificates = createCertChain(NativeCrypto
                    .SSL_get_certificate(sslNativePointer));
            X509Certificate[] peerCertificates = createCertChain(NativeCrypto
                    .SSL_get_peer_cert_chain(sslNativePointer));
            sslSession = new OpenSSLSessionImpl(sslSessionNativePointer, localCertificates,
                    peerCertificates, hostname, port, getSessionContext());
            // if not, putSession later in handshakeCompleted() callback
            if (handshakeCompleted) {
                getSessionContext().putSession(sslSession);
            }
        }
        return sslSession;
    }
}
