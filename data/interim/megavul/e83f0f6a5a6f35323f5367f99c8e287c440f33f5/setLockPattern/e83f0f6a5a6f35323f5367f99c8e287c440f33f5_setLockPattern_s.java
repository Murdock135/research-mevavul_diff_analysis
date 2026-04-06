class setLockPattern {
@Override
    public void setLockPattern(String pattern, String savedCredential, int userId)
            throws RemoteException {
        byte[] currentHandle = getCurrentHandle(userId);

        if (pattern == null) {
            getGateKeeperService().clearSecureUserId(userId);
            mStorage.writePatternHash(null, userId);
            setKeystorePassword(null, userId);
            return;
        }

        if (currentHandle == null) {
            if (savedCredential != null) {
                Slog.w(TAG, "Saved credential provided, but none stored");
            }
            savedCredential = null;
        }

        byte[] enrolledHandle = enrollCredential(currentHandle, savedCredential, pattern, userId);
        if (enrolledHandle != null) {
            mStorage.writePatternHash(enrolledHandle, userId);
        } else {
            Slog.e(TAG, "Failed to enroll pattern");
        }
    }
}
