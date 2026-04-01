class clearInternalData {
private void clearInternalData() {
        mPendingRootCaCertDepth = -1;
        mPendingRootCaCert = null;
        mPendingServerCert = null;
        mPendingServerCertSubjectInfo = null;
        mPendingServerCertIssuerInfo = null;
        mCurrentTofuConfig = null;
    }
}
