class getProcessRecordLocked {
final ProcessRecord getProcessRecordLocked(String processName, int uid, boolean keepIfLarge) {
        if (uid == Process.SYSTEM_UID) {
            // The system gets to run in any process.  If there are multiple
            // processes with the same uid, just pick the first (this
            // should never happen).
            SparseArray<ProcessRecord> procs = mProcessNames.getMap().get(processName);
            if (procs == null) return null;
            final int N = procs.size();
            for (int i = 0; i < N; i++) {
                if (UserHandle.isSameUser(procs.keyAt(i), uid)) return procs.valueAt(i);
            }
        }
        ProcessRecord proc = mProcessNames.get(processName, uid);
        if (false && proc != null && !keepIfLarge
                && proc.setProcState >= ActivityManager.PROCESS_STATE_CACHED_EMPTY
                && proc.lastCachedPss >= 4000) {
            // Turn this condition on to cause killing to happen regularly, for testing.
            if (proc.baseProcessTracker != null) {
                proc.baseProcessTracker.reportCachedKill(proc.pkgList, proc.lastCachedPss);
            }
            proc.kill(Long.toString(proc.lastCachedPss) + "k from cached", true);
        } else if (proc != null && !keepIfLarge
                && mLastMemoryLevel > ProcessStats.ADJ_MEM_FACTOR_NORMAL
                && proc.setProcState >= ActivityManager.PROCESS_STATE_CACHED_EMPTY) {
            if (DEBUG_PSS) Slog.d(TAG, "May not keep " + proc + ": pss=" + proc.lastCachedPss);
            if (proc.lastCachedPss >= mProcessList.getCachedRestoreThresholdKb()) {
                if (proc.baseProcessTracker != null) {
                    proc.baseProcessTracker.reportCachedKill(proc.pkgList, proc.lastCachedPss);
                }
                proc.kill(Long.toString(proc.lastCachedPss) + "k from cached", true);
            }
        }
        return proc;
    }
}
