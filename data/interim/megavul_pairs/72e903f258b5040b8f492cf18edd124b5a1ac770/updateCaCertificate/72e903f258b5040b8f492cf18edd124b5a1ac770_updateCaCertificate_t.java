class updateCaCertificate {
public boolean updateCaCertificate(int networkId, @NonNull X509Certificate caCert,
            @NonNull X509Certificate serverCert, String certHash) {
        WifiConfiguration internalConfig = getInternalConfiguredNetwork(networkId);
        if (internalConfig == null) {
            Log.e(TAG, "No network for network ID " + networkId);
            return false;
        }
        if (!internalConfig.isEnterprise()) {
            Log.e(TAG, "Network " + networkId + " is not an Enterprise network");
            return false;
        }
        if (!internalConfig.enterpriseConfig.isEapMethodServerCertUsed()) {
            Log.e(TAG, "Network " + networkId + " does not need verifying server cert");
            return false;
        }
        if (null == caCert) {
            Log.e(TAG, "Root CA cert is null");
            return false;
        }
        if (null == serverCert) {
            Log.e(TAG, "Server cert is null");
            return false;
        }
        CertificateSubjectInfo serverCertInfo = CertificateSubjectInfo.parse(
                serverCert.getSubjectDN().getName());
        if (null == serverCertInfo) {
            Log.e(TAG, "Invalid Server CA cert subject");
            return false;
        }

        WifiConfiguration newConfig = new WifiConfiguration(internalConfig);
        try {
            if (newConfig.enterpriseConfig.isTrustOnFirstUseEnabled()) {
                if (TextUtils.isEmpty(certHash)) {
                    newConfig.enterpriseConfig.setCaCertificateForTrustOnFirstUse(caCert);
                } else {
                    newConfig.enterpriseConfig.setServerCertificateHash(certHash);
                }
                newConfig.enterpriseConfig.enableTrustOnFirstUse(false);
            } else {
                // setCaCertificate will mark that this CA certificate should be removed on
                // removing this configuration.
                newConfig.enterpriseConfig.setCaCertificate(caCert);
            }
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Failed to set CA cert: " + caCert);
            return false;
        }

        // If there is a subject alternative name, it should be matched first.
        String altSubjectNames = getAltSubjectMatchFromAltSubjectName(serverCert);
        if (!TextUtils.isEmpty(altSubjectNames)) {
            if (mVerboseLoggingEnabled) {
                Log.d(TAG, "Set altSubjectMatch to " + altSubjectNames);
            }
            newConfig.enterpriseConfig.setAltSubjectMatch(altSubjectNames);
        } else {
            if (mVerboseLoggingEnabled) {
                Log.d(TAG, "Set domainSuffixMatch to " + serverCertInfo.commonName);
            }
            newConfig.enterpriseConfig.setDomainSuffixMatch(serverCertInfo.commonName);
        }
        newConfig.enterpriseConfig.setUserApproveNoCaCert(false);
        // Trigger an update to install CA certificate and the corresponding configuration.
        NetworkUpdateResult result = addOrUpdateNetwork(newConfig, internalConfig.creatorUid);
        if (!result.isSuccess()) {
            Log.e(TAG, "Failed to install CA cert for network " + internalConfig.SSID);
            mFrameworkFacade.showToast(mContext, mContext.getResources().getString(
                    R.string.wifi_ca_cert_failed_to_install_ca_cert));
            return false;
        }
        return true;
    }
}
