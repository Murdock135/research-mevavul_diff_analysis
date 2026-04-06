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
            return fakeConnection;
        }
}
