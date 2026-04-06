class startUserApprovalIfNecessary {
public boolean startUserApprovalIfNecessary(boolean isUserSelected) {
        if (null == mConnectingConfig || null == mCurrentTofuConfig) return false;
        if (mConnectingConfig.networkId != mCurrentTofuConfig.networkId) return false;

        // If Trust On First Use is supported and insecure enterprise configuration
        // is not allowed, TOFU must be used for an Enterprise network without certs. This should
        // not happen because the TOFU flag will be set during boot if these conditions are met.
        if (mIsTrustOnFirstUseSupported && !mIsInsecureEnterpriseConfigurationAllowed
                && !mCurrentTofuConfig.enterpriseConfig.isTrustOnFirstUseEnabled()) {
            Log.e(TAG, "Upgrade insecure connection to TOFU.");
            mCurrentTofuConfig.enterpriseConfig.enableTrustOnFirstUse(true);
        }

        if (useTrustOnFirstUse()) {
            if (null == mPendingRootCaCert) {
                Log.e(TAG, "No valid CA cert for TLS-based connection.");
                handleError(mCurrentTofuConfig.SSID);
                return false;
            }
            if (null == mPendingServerCert) {
                Log.e(TAG, "No valid Server cert for TLS-based connection.");
                handleError(mCurrentTofuConfig.SSID);
                return false;
            }
            if (!isServerCertChainValid()) {
                Log.e(TAG, "Server cert chain is invalid.");
                String ssid = mCurrentTofuConfig.SSID;
                handleError(ssid);
                createCertificateErrorNotification(isUserSelected, ssid);
                return false;
            }
        } else if (mIsInsecureEnterpriseConfigurationAllowed) {
            Log.i(TAG, "Insecure networks without a Root CA cert are allowed.");
            return false;
        }

        Log.d(TAG, "startUserApprovalIfNecessaryForInsecureEapNetwork: mIsUserSelected="
                + isUserSelected);

        if (isUserSelected) {
            askForUserApprovalForCaCertificate();
        } else {
            notifyUserForCaCertificate();
        }
        return true;
    }
}
