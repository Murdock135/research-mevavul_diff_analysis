class trimApplications {
final void trimApplications() {
        synchronized (this) {
            int i;

            // First remove any unused application processes whose package
            // has been removed.
            for (i=mRemovedProcesses.size()-1; i>=0; i--) {
                final ProcessRecord app = mRemovedProcesses.get(i);
                if (app.activities.size() == 0
                        && app.curReceiver == null && app.services.size() == 0) {
                    Slog.i(
                        TAG, "Exiting empty application process "
                        + app.toShortString() + " ("
                        + (app.thread != null ? app.thread.asBinder() : null)
                        + ")\n");
                    if (app.pid > 0 && app.pid != MY_PID) {
                        app.kill("empty", false);
                    } else {
                        try {
                            app.thread.scheduleExit();
                        } catch (Exception e) {
                            // Ignore exceptions.
                        }
                    }
                    cleanUpApplicationRecordLocked(app, false, true, -1, false /*replacingPid*/);
                    mRemovedProcesses.remove(i);

                    if (app.persistent) {
                        addAppLocked(app.info, false, null /* ABI override */);
                    }
                }
            }

            // Now update the oom adj for all processes.
            updateOomAdjLocked();
        }
    }
}
