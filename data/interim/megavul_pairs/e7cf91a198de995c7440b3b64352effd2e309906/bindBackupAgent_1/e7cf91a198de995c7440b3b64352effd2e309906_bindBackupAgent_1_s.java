class bindBackupAgent_1 {
public boolean bindBackupAgent(ApplicationInfo app, int backupMode) {
        if (DEBUG_BACKUP) Slog.v(TAG_BACKUP,
                "bindBackupAgent: app=" + app + " mode=" + backupMode);
        enforceCallingPermission("android.permission.CONFIRM_FULL_BACKUP", "bindBackupAgent");

        synchronized(this) {
            // !!! TODO: currently no check here that we're already bound
            BatteryStatsImpl.Uid.Pkg.Serv ss = null;
            BatteryStatsImpl stats = mBatteryStatsService.getActiveStatistics();
            synchronized (stats) {
                ss = stats.getServiceStatsLocked(app.uid, app.packageName, app.name);
            }

            // Backup agent is now in use, its package can't be stopped.
            try {
                AppGlobals.getPackageManager().setPackageStoppedState(
                        app.packageName, false, UserHandle.getUserId(app.uid));
            } catch (RemoteException e) {
            } catch (IllegalArgumentException e) {
                Slog.w(TAG, "Failed trying to unstop package "
                        + app.packageName + ": " + e);
            }

            BackupRecord r = new BackupRecord(ss, app, backupMode);
            ComponentName hostingName = (backupMode == IApplicationThread.BACKUP_MODE_INCREMENTAL)
                    ? new ComponentName(app.packageName, app.backupAgentName)
                    : new ComponentName("android", "FullBackupAgent");
            // startProcessLocked() returns existing proc's record if it's already running
            ProcessRecord proc = startProcessLocked(app.processName, app,
                    false, 0, "backup", hostingName, false, false, false);
            if (proc == null) {
                Slog.e(TAG, "Unable to start backup agent process " + r);
                return false;
            }

            r.app = proc;
            mBackupTarget = r;
            mBackupAppName = app.packageName;

            // Try not to kill the process during backup
            updateOomAdjLocked(proc);

            // If the process is already attached, schedule the creation of the backup agent now.
            // If it is not yet live, this will be done when it attaches to the framework.
            if (proc.thread != null) {
                if (DEBUG_BACKUP) Slog.v(TAG_BACKUP, "Agent proc already running: " + proc);
                try {
                    proc.thread.scheduleCreateBackupAgent(app,
                            compatibilityInfoForPackageLocked(app), backupMode);
                } catch (RemoteException e) {
                    // Will time out on the backup manager side
                }
            } else {
                if (DEBUG_BACKUP) Slog.v(TAG_BACKUP, "Agent proc not running, waiting for attach");
            }
            // Invariants: at this point, the target app process exists and the application
            // is either already running or in the process of coming up.  mBackupTarget and
            // mBackupAppName describe the app, so that when it binds back to the AM we
            // know that it's scheduled for a backup-agent operation.
        }

        return true;
    }
}
