class setLockPassword {
@Override
    public void setLockPassword(String password, String savedCredential, int userId)
            throws RemoteException {
        byte[] currentHandle = getCurrentHandle(userId);

        if (password == null) {
            getGateKeeperService().clearSecureUserId(userId);
            mStorage.writePasswordHash(null, userId);
            setKeystorePassword(null, userId);
            return;
        }

        if (currentHandle == null) {
            if (savedCredential != null) {
                Slog.w(TAG, "Saved credential provided, but none stored");
            }
            savedCredential = null;
        }

        byte[] enrolledHandle = enrollCredential(currentHandle, savedCredential, password, userId);
        if (enrolledHandle != null) {
            mStorage.writePasswordHash(enrolledHandle, userId);
        } else {
            Slog.e(TAG, "Failed to enroll password");
        }
    }
}
