class startOneTimePermissionSession {
@Override
    public void startOneTimePermissionSession(String packageName, @UserIdInt int userId,
            long timeoutMillis, long revokeAfterKilledDelayMillis) {
        mContext.enforceCallingOrSelfPermission(
                Manifest.permission.MANAGE_ONE_TIME_PERMISSION_SESSIONS,
                "Must hold " + Manifest.permission.MANAGE_ONE_TIME_PERMISSION_SESSIONS
                        + " to register permissions as one time.");
        Objects.requireNonNull(packageName);

        final long token = Binder.clearCallingIdentity();
        try {
            getOneTimePermissionUserManager(userId).startPackageOneTimeSession(packageName,
                    timeoutMillis, revokeAfterKilledDelayMillis);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }
}
