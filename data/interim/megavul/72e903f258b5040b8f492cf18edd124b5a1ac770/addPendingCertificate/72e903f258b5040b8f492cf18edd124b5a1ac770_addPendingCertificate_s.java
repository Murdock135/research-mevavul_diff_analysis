class addPendingCertificate {
public boolean addPendingCertificate(@NonNull String ssid, int depth,
            @NonNull X509Certificate cert) {
        String configProfileKey = mCurrentTofuConfig != null
                ? mCurrentTofuConfig.getProfileKey() : "null";
        Log.d(TAG, "setPendingCertificate: " + "ssid=" + ssid + " depth=" + depth
                + " current config=" + configProfileKey);
        if (TextUtils.isEmpty(ssid)) return false;
        if (null == mCurrentTofuConfig) return false;
        if (!TextUtils.equals(ssid, mCurrentTofuConfig.SSID)) return false;
        if (null == cert) return false;
        if (depth < 0) return false;

        // If TOFU is not supported return immediately, although this should not happen since
        // the caller code flow is only active when TOFU is supported.
        if (!mIsTrustOnFirstUseSupported) return false;

        // If insecure configurations are allowed and this configuration is configured with
        // "Do not validate" (i.e. TOFU is disabled), skip loading the certificates (no need for
        // them anyway) and don't disconnect the network.
        if (mIsInsecureEnterpriseConfigurationAllowed
                && !mCurrentTofuConfig.enterpriseConfig.isTrustOnFirstUseEnabled()) {
            Log.d(TAG, "Certificates are not required for this connection");
            return false;
        }

        if (depth == 0) {
            // Disable network selection upon receiving the server certificate
            putNetworkOnHold();
        }

        if (!mServerCertChain.contains(cert)) {
            mServerCertChain.add(cert);
        }

        // 0 is the tail, i.e. the server cert.
        if (depth == 0 && null == mPendingServerCert) {
            mPendingServerCert = cert;
            Log.d(TAG, "Pending server certificate: " + mPendingServerCert);
            mPendingServerCertSubjectInfo = CertificateSubjectInfo.parse(
                    cert.getSubjectX500Principal().getName());
            if (null == mPendingServerCertSubjectInfo) {
                Log.e(TAG, "CA cert has no valid subject.");
                return false;
            }
            mPendingServerCertIssuerInfo = CertificateSubjectInfo.parse(
                    cert.getIssuerX500Principal().getName());
            if (null == mPendingServerCertIssuerInfo) {
                Log.e(TAG, "CA cert has no valid issuer.");
                return false;
            }
        }

        // Root or intermediate cert.
        if (depth < mPendingRootCaCertDepth) {
            Log.d(TAG, "Ignore intermediate cert." + cert);
            return true;
        }
        mPendingRootCaCertDepth = depth;
        mPendingRootCaCert = cert;
        Log.d(TAG, "Pending Root CA certificate: " + mPendingRootCaCert);
        return true;
    }
}
