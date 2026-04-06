class getSSLContext {
public SSLContext getSSLContext(boolean acceptAnyCertificate) throws GeneralSecurityException, IOException {
        return acceptAnyCertificate? looseTrustManagerSSLContext: SSLContext.getDefault();
    }
}
