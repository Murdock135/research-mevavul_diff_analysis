class handshake {
private void handshake() throws WebSocketException
    {
        try
        {
            // Perform handshake with the proxy server.
            mProxyHandshaker.perform();
        }
        catch (IOException e)
        {
            // Handshake with the proxy server failed.
            String message = String.format(
                "Handshake with the proxy server (%s) failed: %s", mAddress, e.getMessage());

            // Raise an exception with PROXY_HANDSHAKE_ERROR.
            throw new WebSocketException(WebSocketError.PROXY_HANDSHAKE_ERROR, message, e);
        }

        if (mSSLSocketFactory == null)
        {
            // SSL handshake with the WebSocket endpoint is not needed.
            return;
        }

        try
        {
            // Overlay the existing socket.
            mSocket = mSSLSocketFactory.createSocket(mSocket, mHost, mPort, true);
        }
        catch (IOException e)
        {
            // Failed to overlay an existing socket.
            String message = "Failed to overlay an existing socket: " + e.getMessage();

            // Raise an exception with SOCKET_OVERLAY_ERROR.
            throw new WebSocketException(WebSocketError.SOCKET_OVERLAY_ERROR, message, e);
        }

        try
        {
            // Start the SSL handshake manually. As for the reason, see
            // http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/samples/sockets/client/SSLSocketClient.java
            ((SSLSocket)mSocket).startHandshake();
            
            if (mSocket instanceof SSLSocket)
            {
                // Verify that the proxied hostname matches the certificate here since
                // this is not automatically done by the SSLSocket.
                OkHostnameVerifier hostnameVerifier = OkHostnameVerifier.INSTANCE;
                
                SSLSession sslSession = ((SSLSocket) mSocket).getSession();
            
                if (!hostnameVerifier.verify(mProxyHandshaker.getProxiedHostname(), sslSession))
                {
                    throw new SSLPeerUnverifiedException("Proxied hostname " + mProxyHandshaker.getProxiedHostname()
                            + " does not match certificate (" + sslSession.getPeerPrincipal() + ")");
                }
            }
        }
        catch (IOException e)
        {
            // SSL handshake with the WebSocket endpoint failed.
            String message = String.format(
                "SSL handshake with the WebSocket endpoint (%s) failed: %s", mAddress, e.getMessage());

            // Raise an exception with SSL_HANDSHAKE_ERROR.
            throw new WebSocketException(WebSocketError.SSL_HANDSHAKE_ERROR, message, e);
        }
    }
}
