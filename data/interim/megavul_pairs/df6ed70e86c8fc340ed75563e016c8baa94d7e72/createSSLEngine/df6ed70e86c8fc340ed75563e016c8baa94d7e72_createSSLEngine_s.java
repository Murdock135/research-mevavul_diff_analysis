class createSSLEngine {
private SSLEngine createSSLEngine() throws IOException, GeneralSecurityException {
        
        if (nettyProviderConfig.getSslEngineFactory() != null) {
            return nettyProviderConfig.getSslEngineFactory().newSSLEngine();
        
        } else {
            SSLContext sslContext = config.getSSLContext();
            if (sslContext == null) {
                sslContext = SslUtils.getInstance().getSSLContext();
            }
            
            SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(true);
            return sslEngine;
        }
    }
}
