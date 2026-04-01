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
