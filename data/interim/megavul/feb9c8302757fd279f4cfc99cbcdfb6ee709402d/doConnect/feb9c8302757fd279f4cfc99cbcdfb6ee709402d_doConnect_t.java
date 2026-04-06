class doConnect {
private void doConnect() throws WebSocketException
    {
        // True if a proxy server is set.
        boolean proxied = mProxyHandshaker != null;

        try
        {
            // Connect to the server (either a proxy or a WebSocket endpoint).
            mSocket.connect(mAddress.toInetSocketAddress(), mConnectionTimeout);
            
            if (mSocket instanceof SSLSocket)
            {
                // Verify that the hostname matches the certificate here since
                // this is not automatically done by the SSLSocket.
                OkHostnameVerifier hostnameVerifier = OkHostnameVerifier.INSTANCE;
                
                SSLSession sslSession = ((SSLSocket) mSocket).getSession();
            
                if (!hostnameVerifier.verify(mAddress.getHostname(), sslSession))
                {
                    throw new SSLPeerUnverifiedException("Hostname does not match certificate ("
                            + sslSession.getPeerPrincipal() + ")");
                }
            }
        }
        catch (IOException e)
        {
            // Failed to connect the server.
            String message = String.format("Failed to connect to %s'%s': %s",
                (proxied ? "the proxy " : ""), mAddress, e.getMessage());

            // Raise an exception with SOCKET_CONNECT_ERROR.
            throw new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR, message, e);
        }

        // If a proxy server is set.
        if (proxied)
        {
            // Perform handshake with the proxy server.
            // SSL handshake is performed as necessary, too.
            handshake();
        }
    }
}
