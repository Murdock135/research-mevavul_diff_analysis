class onCreateOutgoingConnection {
@Override
        public Connection onCreateOutgoingConnection(
                PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
            FakeConnection fakeConnection = new FakeConnection(request.getVideoState(),
                    request.getAddress());
            mLatestConnection = fakeConnection;
            if (mCapabilities != NOT_SPECIFIED) {
                fakeConnection.setConnectionCapabilities(mCapabilities);
            }
            if (mProperties != NOT_SPECIFIED) {
                fakeConnection.setConnectionProperties(mProperties);
            }
            // Testing for StatusHints image icon cross user access
            if (request.getExtras() != null) {
                fakeConnection.setStatusHints(
                        request.getExtras().getParcelable(STATUS_HINTS_EXTRA));
            }
            return fakeConnection;
        }
}
