class setStatusHints {
@Override
        public void setStatusHints(String callId, StatusHints statusHints,
                Session.Info sessionInfo) {
            Log.startSession(sessionInfo, "CSW.sSH", mPackageAbbreviation);
            UserHandle callingUserHandle = Binder.getCallingUserHandle();
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (mLock) {
                    logIncoming("setStatusHints %s %s", callId, statusHints);
                    // Check status hints image for cross user access
                    if (statusHints != null) {
                        Icon icon = statusHints.getIcon();
                        statusHints.setIcon(StatusHints.validateAccountIconUserBoundary(
                                icon, callingUserHandle));
                    }
                    Call call = mCallIdMapper.getCall(callId);
                    if (call != null) {
                        call.setStatusHints(statusHints);
                    }
                }
            } catch (Throwable t) {
                Log.e(ConnectionServiceWrapper.this, t, "");
                throw t;
            } finally {
                Binder.restoreCallingIdentity(token);
                Log.endSession();
            }
        }
}
