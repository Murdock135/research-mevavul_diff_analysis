class testOutgoingVideoCallAnsweredAsAudio {
@LargeTest
    @Test
    public void testOutgoingVideoCallAnsweredAsAudio() throws Exception {
        IdPair ids = startOutgoingPhoneCall("650-555-1212", mPhoneAccountA0.getAccountHandle(),
                mConnectionServiceFixtureA, Process.myUserHandle(),
                VideoProfile.STATE_BIDIRECTIONAL, null);
        com.android.server.telecom.Call call = mTelecomSystem.getCallsManager().getCalls()
                .iterator().next();

        mConnectionServiceFixtureA.mConnectionById.get(ids.mConnectionId).videoState
                = VideoProfile.STATE_AUDIO_ONLY;
        mConnectionServiceFixtureA.sendSetVideoState(ids.mConnectionId);
        mConnectionServiceFixtureA.sendSetActive(ids.mConnectionId);

        assertFalse(VideoProfile.isVideo(call.getVideoStateHistory()));
    }
}
