class isGetTasksAllowed {
private boolean isGetTasksAllowed(String caller, int callingPid, int callingUid) {
        boolean allowed = checkPermission(android.Manifest.permission.REAL_GET_TASKS,
                callingPid, callingUid) == PackageManager.PERMISSION_GRANTED;
        if (!allowed) {
            if (checkPermission(android.Manifest.permission.GET_TASKS,
                    callingPid, callingUid) == PackageManager.PERMISSION_GRANTED) {
                // Temporary compatibility: some existing apps on the system image may
                // still be requesting the old permission and not switched to the new
                // one; if so, we'll still allow them full access.  This means we need
                // to see if they are holding the old permission and are a system app.
                try {
                    if (AppGlobals.getPackageManager().isUidPrivileged(callingUid)) {
                        allowed = true;
                        Slog.w(TAG, caller + ": caller " + callingUid
                                + " is using old GET_TASKS but privileged; allowing");
                    }
                } catch (RemoteException e) {
                }
            }
        }
        if (!allowed) {
            Slog.w(TAG, caller + ": caller " + callingUid
                    + " does not hold GET_TASKS; limiting output");
        }
        return allowed;
    }
}
