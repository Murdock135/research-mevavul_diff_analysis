class startPackageOneTimeSession {
void startPackageOneTimeSession(@NonNull String packageName, long timeoutMillis,
            long revokeAfterKilledDelayMillis, int importanceToResetTimer,
            int importanceToKeepSessionAlive) {
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
                listener.updateSessionParameters(timeoutMillis, revokeAfterKilledDelayMillis,
                        importanceToResetTimer, importanceToKeepSessionAlive);
                return;
            }
            listener = new PackageInactivityListener(uid, packageName, timeoutMillis,
                    revokeAfterKilledDelayMillis, importanceToResetTimer,
                    importanceToKeepSessionAlive);
            mListeners.put(uid, listener);
        }
    }
}
