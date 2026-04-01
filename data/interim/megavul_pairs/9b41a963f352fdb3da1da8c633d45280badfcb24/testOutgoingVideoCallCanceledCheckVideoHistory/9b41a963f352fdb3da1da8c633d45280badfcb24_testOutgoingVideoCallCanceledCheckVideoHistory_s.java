class testOutgoingVideoCallCanceledCheckVideoHistory {
@LargeTest
    @Test
    public void testOutgoingVideoCallCanceledCheckVideoHistory() throws Exception {
        IdPair ids = startOutgoingPhoneCall("650-555-1212", mPhoneAccountA0.getAccountHandle(),
                mConnectionServiceFixtureA, Process.myUserHandle(),
                VideoProfile.STATE_BIDIRECTIONAL);
        com.android.server.telecom.Call call = mTelecomSystem.getCallsManager().getCalls()
                .iterator().next();

        mConnectionServiceFixtureA.sendSetDisconnected(ids.mConnectionId, DisconnectCause.LOCAL);

        assertTrue(VideoProfile.isVideo(call.getVideoStateHistory()));
    }
}
