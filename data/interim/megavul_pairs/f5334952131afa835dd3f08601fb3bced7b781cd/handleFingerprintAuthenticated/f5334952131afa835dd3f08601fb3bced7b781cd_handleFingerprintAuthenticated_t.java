class handleFingerprintAuthenticated {
private void handleFingerprintAuthenticated(int authUserId) {
        try {
            final int userId;
            try {
                userId = ActivityManagerNative.getDefault().getCurrentUser().id;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to get current user id: ", e);
                return;
            }
            if (userId != authUserId) {
                Log.d(TAG, "Fingerprint authenticated for wrong user: " + authUserId);
                return;
            }
            if (isFingerprintDisabled(userId)) {
                Log.d(TAG, "Fingerprint disabled by DPM for userId: " + userId);
                return;
            }
            onFingerprintAuthenticated(userId);
        } finally {
            setFingerprintRunningState(FINGERPRINT_STATE_STOPPED);
        }
    }
}
