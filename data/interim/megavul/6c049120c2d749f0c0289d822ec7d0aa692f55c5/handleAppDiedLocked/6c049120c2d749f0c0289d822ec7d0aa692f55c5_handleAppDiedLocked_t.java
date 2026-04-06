class handleAppDiedLocked {
private final void handleAppDiedLocked(ProcessRecord app,
            boolean restarting, boolean allowRestart) {
        int pid = app.pid;
        boolean kept = cleanUpApplicationRecordLocked(app, restarting, allowRestart, -1,
                false /*replacingPid*/);
        if (!kept && !restarting) {
            removeLruProcessLocked(app);
            if (pid > 0) {
                ProcessList.remove(pid);
            }
        }

        if (mProfileProc == app) {
            clearProfilerLocked();
        }

        // Remove this application's activities from active lists.
        boolean hasVisibleActivities = mStackSupervisor.handleAppDiedLocked(app);

        app.activities.clear();

        if (app.instrumentationClass != null) {
            Slog.w(TAG, "Crash of app " + app.processName
                  + " running instrumentation " + app.instrumentationClass);
            Bundle info = new Bundle();
            info.putString("shortMsg", "Process crashed.");
            finishInstrumentationLocked(app, Activity.RESULT_CANCELED, info);
        }

        if (!restarting && hasVisibleActivities
                && !mStackSupervisor.resumeFocusedStackTopActivityLocked()) {
            // If there was nothing to resume, and we are not already restarting this process, but
            // there is a visible activity that is hosted by the process...  then make sure all
            // visible activities are running, taking care of restarting this process.
            mStackSupervisor.ensureActivitiesVisibleLocked(null, 0, !PRESERVE_WINDOWS);
        }
    }
}
