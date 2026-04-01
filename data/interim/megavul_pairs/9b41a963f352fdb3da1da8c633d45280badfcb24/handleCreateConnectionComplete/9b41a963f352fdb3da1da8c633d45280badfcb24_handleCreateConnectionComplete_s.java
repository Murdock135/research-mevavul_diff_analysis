class handleCreateConnectionComplete {
@Override
        public void handleCreateConnectionComplete(String callId, ConnectionRequest request,
                ParcelableConnection connection, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, LogUtils.Sessions.CSW_HANDLE_CREATE_CONNECTION_COMPLETE,
                    mPackageAbbreviation);
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (mLock) {
                    logIncoming("handleCreateConnectionComplete %s", callId);
                    ConnectionServiceWrapper.this
                            .handleCreateConnectionComplete(callId, request, connection);

                    if (mServiceInterface != null) {
                        logOutgoing("createConnectionComplete %s", callId);
                        try {
                            mServiceInterface.createConnectionComplete(callId,
                                    Log.getExternalSession());
                        } catch (RemoteException e) {
                            logOutgoing("createConnectionComplete remote exception=%s", e);
                        }
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
