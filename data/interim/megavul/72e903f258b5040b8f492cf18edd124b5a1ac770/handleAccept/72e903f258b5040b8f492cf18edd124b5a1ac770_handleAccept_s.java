class handleAccept {
@VisibleForTesting
    void handleAccept(@NonNull String ssid) {
        if (!isConnectionValid(ssid)) return;

        if (!useTrustOnFirstUse()) {
            mWifiConfigManager.setUserApproveNoCaCert(mCurrentTofuConfig.networkId, true);
        } else {
            if (null == mPendingRootCaCert || null == mPendingServerCert) {
                handleError(ssid);
                return;
            }
            if (!mWifiConfigManager.updateCaCertificate(
                    mCurrentTofuConfig.networkId, mPendingRootCaCert, mPendingServerCert)) {
                // The user approved this network,
                // keep the connection regardless of the result.
                Log.e(TAG, "Cannot update CA cert to network " + mCurrentTofuConfig.getProfileKey()
                        + ", CA cert = " + mPendingRootCaCert);
            }
        }
        mWifiConfigManager.updateNetworkSelectionStatus(mCurrentTofuConfig.networkId,
                WifiConfiguration.NetworkSelectionStatus.DISABLED_NONE);
        dismissDialogAndNotification();
        clearInternalData();

        if (null != mCallbacks) mCallbacks.onAccept(ssid);
    }
}
