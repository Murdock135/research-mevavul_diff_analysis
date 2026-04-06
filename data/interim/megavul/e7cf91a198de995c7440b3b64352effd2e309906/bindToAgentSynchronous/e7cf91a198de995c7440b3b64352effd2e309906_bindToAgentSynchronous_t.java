class bindToAgentSynchronous {
IBackupAgent bindToAgentSynchronous(ApplicationInfo app, int mode) {
        IBackupAgent agent = null;
        synchronized(mAgentConnectLock) {
            mConnecting = true;
            mConnectedAgent = null;
            try {
                if (mActivityManager.bindBackupAgent(app.packageName, mode,
                        UserHandle.USER_OWNER)) {
                    Slog.d(TAG, "awaiting agent for " + app);

                    // success; wait for the agent to arrive
                    // only wait 10 seconds for the bind to happen
                    long timeoutMark = System.currentTimeMillis() + TIMEOUT_INTERVAL;
                    while (mConnecting && mConnectedAgent == null
                            && (System.currentTimeMillis() < timeoutMark)) {
                        try {
                            mAgentConnectLock.wait(5000);
                        } catch (InterruptedException e) {
                            // just bail
                            Slog.w(TAG, "Interrupted: " + e);
                            mActivityManager.clearPendingBackup();
                            return null;
                        }
                    }

                    // if we timed out with no connect, abort and move on
                    if (mConnecting == true) {
                        Slog.w(TAG, "Timeout waiting for agent " + app);
                        mActivityManager.clearPendingBackup();
                        return null;
                    }
                    if (DEBUG) Slog.i(TAG, "got agent " + mConnectedAgent);
                    agent = mConnectedAgent;
                }
            } catch (RemoteException e) {
                // can't happen - ActivityManager is local
            }
        }
        return agent;
    }
}
