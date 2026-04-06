class killAllBackgroundProcesses {
@Override
    public void killAllBackgroundProcesses() {
        if (checkCallingPermission(android.Manifest.permission.KILL_BACKGROUND_PROCESSES)
                != PackageManager.PERMISSION_GRANTED) {
            final String msg = "Permission Denial: killAllBackgroundProcesses() from pid="
                    + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid()
                    + " requires " + android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        }

        final int callingUid = Binder.getCallingUid();
        final int callingPid = Binder.getCallingPid();

        ProcessRecord proc;
        synchronized (mPidsSelfLocked) {
            proc = mPidsSelfLocked.get(callingPid);
        }
        if (callingUid >= FIRST_APPLICATION_UID
                && (proc == null || !proc.info.isSystemApp())) {
            final String msg = "Permission Denial: killAllBackgroundProcesses() from pid="
                    + callingPid + ", uid=" + callingUid + " is not allowed";
            Slog.w(TAG, msg);
            // Silently return to avoid existing apps from crashing.
            return;
        }

        final long callingId = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                // Allow memory level to go down (the flag needs to be set before updating oom adj)
                // because this method is also used to simulate low memory.
                mAppProfiler.setAllowLowerMemLevelLocked(true);
                synchronized (mProcLock) {
                    mProcessList.killPackageProcessesLSP(null /* packageName */, -1 /* appId */,
                            UserHandle.USER_ALL, ProcessList.CACHED_APP_MIN_ADJ,
                            ApplicationExitInfo.REASON_USER_REQUESTED,
                            ApplicationExitInfo.SUBREASON_KILL_BACKGROUND,
                            "kill all background");
                }

                mAppProfiler.doLowMemReportIfNeededLocked(null);
            }
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }
}
