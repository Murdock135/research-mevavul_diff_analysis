class getRunningAppProcesses {
public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() {
        enforceNotIsolatedCaller("getRunningAppProcesses");
        // Lazy instantiation of list
        List<ActivityManager.RunningAppProcessInfo> runList = null;
        final boolean allUsers = ActivityManager.checkUidPermission(INTERACT_ACROSS_USERS_FULL,
                Binder.getCallingUid()) == PackageManager.PERMISSION_GRANTED;
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this) {
            // Iterate across all processes
            for (int i=mLruProcesses.size()-1; i>=0; i--) {
                ProcessRecord app = mLruProcesses.get(i);
                if (!allUsers && app.userId != userId) {
                    continue;
                }
                if ((app.thread != null) && (!app.crashing && !app.notResponding)) {
                    // Generate process state info for running application
                    ActivityManager.RunningAppProcessInfo currApp = 
                        new ActivityManager.RunningAppProcessInfo(app.processName,
                                app.pid, app.getPackageList());
                    fillInProcMemInfo(app, currApp);
                    if (app.adjSource instanceof ProcessRecord) {
                        currApp.importanceReasonPid = ((ProcessRecord)app.adjSource).pid;
                        currApp.importanceReasonImportance =
                                ActivityManager.RunningAppProcessInfo.procStateToImportance(
                                        app.adjSourceProcState);
                    } else if (app.adjSource instanceof ActivityRecord) {
                        ActivityRecord r = (ActivityRecord)app.adjSource;
                        if (r.app != null) currApp.importanceReasonPid = r.app.pid;
                    }
                    if (app.adjTarget instanceof ComponentName) {
                        currApp.importanceReasonComponent = (ComponentName)app.adjTarget;
                    }
                    //Slog.v(TAG, "Proc " + app.processName + ": imp=" + currApp.importance
                    //        + " lru=" + currApp.lru);
                    if (runList == null) {
                        runList = new ArrayList<ActivityManager.RunningAppProcessInfo>();
                    }
                    runList.add(currApp);
                }
            }
        }
        return runList;
    }
}
