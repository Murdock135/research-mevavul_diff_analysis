class startPackageOneTimeSession {
void startPackageOneTimeSession(@NonNull String packageName, long timeoutMillis,
            long revokeAfterKilledDelayMillis) {
        int uid;
        try {
            uid = mContext.getPackageManager().getPackageUid(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "Unknown package name " + packageName, e);
            return;
        }

        synchronized (mLock) {
            PackageInactivityListener listener = mListeners.get(uid);
            if (listener != null) {
                listener.updateSessionParameters(timeoutMillis, revokeAfterKilledDelayMillis);
                return;
            }
            listener = new PackageInactivityListener(uid, packageName, timeoutMillis,
                    revokeAfterKilledDelayMillis);
            mListeners.put(uid, listener);
        }
    }
}
