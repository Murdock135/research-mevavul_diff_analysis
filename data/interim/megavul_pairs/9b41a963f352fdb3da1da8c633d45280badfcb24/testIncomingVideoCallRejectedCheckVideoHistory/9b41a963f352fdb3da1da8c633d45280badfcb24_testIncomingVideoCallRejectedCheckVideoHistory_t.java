class testIncomingVideoCallRejectedCheckVideoHistory {
@LargeTest
    @Test
    public void testIncomingVideoCallRejectedCheckVideoHistory() throws Exception {
        IdPair ids = startIncomingPhoneCall("650-555-1212", mPhoneAccountA0.getAccountHandle(),
                VideoProfile.STATE_BIDIRECTIONAL, mConnectionServiceFixtureA, null);
        com.android.server.telecom.Call call = mTelecomSystem.getCallsManager().getCalls()
                .iterator().next();

        mConnectionServiceFixtureA.sendSetDisconnected(ids.mConnectionId, DisconnectCause.REJECTED);

        assertTrue(VideoProfile.isVideo(call.getVideoStateHistory()));
    }
}
