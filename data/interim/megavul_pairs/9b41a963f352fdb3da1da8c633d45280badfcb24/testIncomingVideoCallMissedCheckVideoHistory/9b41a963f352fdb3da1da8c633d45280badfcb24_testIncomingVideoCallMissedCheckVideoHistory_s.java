class testIncomingVideoCallMissedCheckVideoHistory {
@LargeTest
    @Test
    public void testIncomingVideoCallMissedCheckVideoHistory() throws Exception {
        IdPair ids = startIncomingPhoneCall("650-555-1212", mPhoneAccountA0.getAccountHandle(),
                VideoProfile.STATE_BIDIRECTIONAL, mConnectionServiceFixtureA);
        com.android.server.telecom.Call call = mTelecomSystem.getCallsManager().getCalls()
                .iterator().next();

        mConnectionServiceFixtureA.sendSetDisconnected(ids.mConnectionId, DisconnectCause.MISSED);

        assertTrue(VideoProfile.isVideo(call.getVideoStateHistory()));
    }
}
