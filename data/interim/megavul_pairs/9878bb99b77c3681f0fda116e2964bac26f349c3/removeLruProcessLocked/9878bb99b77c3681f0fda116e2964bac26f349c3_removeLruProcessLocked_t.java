class removeLruProcessLocked {
final void removeLruProcessLocked(ProcessRecord app) {
        int lrui = mLruProcesses.lastIndexOf(app);
        if (lrui >= 0) {
            if (!app.killed) {
                Slog.wtfStack(TAG, "Removing process that hasn't been killed: " + app);
                Process.killProcessQuiet(app.pid);
                killProcessGroup(app.uid, app.pid);
            }
            if (lrui <= mLruProcessActivityStart) {
                mLruProcessActivityStart--;
            }
            if (lrui <= mLruProcessServiceStart) {
                mLruProcessServiceStart--;
            }
            mLruProcesses.remove(lrui);
        }
    }
}
