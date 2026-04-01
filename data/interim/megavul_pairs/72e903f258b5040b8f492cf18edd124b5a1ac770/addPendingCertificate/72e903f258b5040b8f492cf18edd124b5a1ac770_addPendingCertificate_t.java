class addPendingCertificate {
public boolean addPendingCertificate(@NonNull String ssid, int depth,
            @NonNull CertificateEventInfo certInfo) {
        String configProfileKey = mCurrentTofuConfig != null
                ? mCurrentTofuConfig.getProfileKey() : "null";
        if (TextUtils.isEmpty(ssid)) return false;
        if (null == mCurrentTofuConfig) return false;
        if (!TextUtils.equals(ssid, mCurrentTofuConfig.SSID)) return false;
        if (null == certInfo) return false;
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

        if (!mServerCertChain.contains(certInfo.getCert())) {
            mServerCertChain.addFirst(certInfo.getCert());
            Log.d(TAG, "addPendingCertificate: " + "SSID=" + ssid + " depth=" + depth
                    + " certHash=" + certInfo.getCertHash() + " current config=" + configProfileKey
                    + "\ncertificate content:\n" + certInfo.getCert());
        }

        // 0 is the tail, i.e. the server cert.
        if (depth == 0 && null == mPendingServerCert) {
            mPendingServerCert = certInfo.getCert();
            mPendingServerCertSubjectInfo = CertificateSubjectInfo.parse(
                    certInfo.getCert().getSubjectX500Principal().getName());
            if (null == mPendingServerCertSubjectInfo) {
                Log.e(TAG, "Cert has no valid subject.");
                return false;
            }
            mPendingServerCertIssuerInfo = CertificateSubjectInfo.parse(
                    certInfo.getCert().getIssuerX500Principal().getName());
            if (null == mPendingServerCertIssuerInfo) {
                Log.e(TAG, "Cert has no valid issuer.");
                return false;
            }
            mServerCertHash = certInfo.getCertHash();
        }

        // Root or intermediate cert.
        if (depth < mPendingRootCaCertDepth) {
            return true;
        }
        mPendingRootCaCertDepth = depth;
        mPendingRootCaCert = certInfo.getCert();

        return true;
    }
}
