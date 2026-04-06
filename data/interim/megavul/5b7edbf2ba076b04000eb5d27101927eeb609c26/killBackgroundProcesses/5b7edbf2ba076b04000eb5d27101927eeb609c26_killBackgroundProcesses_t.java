class killBackgroundProcesses {
@Override
    public void killBackgroundProcesses(final String packageName, int userId) {
        if (checkCallingPermission(android.Manifest.permission.KILL_BACKGROUND_PROCESSES)
                != PackageManager.PERMISSION_GRANTED &&
                checkCallingPermission(android.Manifest.permission.RESTART_PACKAGES)
                        != PackageManager.PERMISSION_GRANTED) {
            String msg = "Permission Denial: killBackgroundProcesses() from pid="
                    + Binder.getCallingPid()
                    + ", uid=" + Binder.getCallingUid()
                    + " requires " + android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        }
        final int callingUid = Binder.getCallingUid();
        final int callingPid = Binder.getCallingPid();
        final int callingAppId = UserHandle.getAppId(callingUid);

        ProcessRecord proc;
        synchronized (mPidsSelfLocked) {
            proc = mPidsSelfLocked.get(callingPid);
        }
        final boolean hasKillAllPermission = PERMISSION_GRANTED == checkPermission(
                android.Manifest.permission.FORCE_STOP_PACKAGES, callingPid, callingUid)
                || UserHandle.isCore(callingUid)
                || (proc != null && proc.info.isSystemApp());

        userId = mUserController.handleIncomingUser(callingPid, callingUid,
                userId, true, ALLOW_FULL_ONLY, "killBackgroundProcesses", null);
        final int[] userIds = mUserController.expandUserId(userId);

        final long callingId = Binder.clearCallingIdentity();
        try {
            IPackageManager pm = AppGlobals.getPackageManager();
            for (int targetUserId : userIds) {
                int appId = -1;
                try {
                    appId = UserHandle.getAppId(
                            pm.getPackageUid(packageName, MATCH_DEBUG_TRIAGED_MISSING,
                                    targetUserId));
                } catch (RemoteException e) {
                }
                if (appId == -1 || (!hasKillAllPermission && appId != callingAppId)) {
                    Slog.w(TAG, "Invalid packageName: " + packageName);
                    return;
                }
                synchronized (this) {
                    synchronized (mProcLock) {
                        mProcessList.killPackageProcessesLSP(packageName, appId, targetUserId,
                                ProcessList.SERVICE_ADJ, ApplicationExitInfo.REASON_USER_REQUESTED,
                                ApplicationExitInfo.SUBREASON_KILL_BACKGROUND, "kill background");
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }
}
