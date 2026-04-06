class testOutgoingCallSelectPhoneAccountNoVideo {
@FlakyTest
    @LargeTest
    @Test
    public void testOutgoingCallSelectPhoneAccountNoVideo() throws Exception {
        startOutgoingPhoneCallPendingCreateConnection("650-555-1212",
                null, mConnectionServiceFixtureA,
                Process.myUserHandle(), VideoProfile.STATE_BIDIRECTIONAL, null);
        com.android.server.telecom.Call call = mTelecomSystem.getCallsManager().getCalls()
                .iterator().next();
        assert(call.isVideoCallingSupportedByPhoneAccount());
        assertEquals(VideoProfile.STATE_BIDIRECTIONAL, call.getVideoState());

        // Change the phone account to one which does not support video calling.
        call.setTargetPhoneAccount(mPhoneAccountA2.getAccountHandle());
        assert(!call.isVideoCallingSupportedByPhoneAccount());
        assertEquals(VideoProfile.STATE_AUDIO_ONLY, call.getVideoState());
    }
}
