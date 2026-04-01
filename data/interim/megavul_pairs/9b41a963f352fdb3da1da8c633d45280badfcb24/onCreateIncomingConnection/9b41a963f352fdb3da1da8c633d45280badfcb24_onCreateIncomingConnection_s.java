class onCreateIncomingConnection {
@Override
        public Connection onCreateIncomingConnection(
                PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
            FakeConnection fakeConnection =  new FakeConnection(
                    mVideoState == INVALID_VIDEO_STATE ? request.getVideoState() : mVideoState,
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
