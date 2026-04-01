class testOutgoingCallSelectPhoneAccountVideo {
@LargeTest
    @Test
    public void testOutgoingCallSelectPhoneAccountVideo() throws Exception {
        startOutgoingPhoneCallPendingCreateConnection("650-555-1212",
                null, mConnectionServiceFixtureA,
                Process.myUserHandle(), VideoProfile.STATE_BIDIRECTIONAL, null);
        com.android.server.telecom.Call call = mTelecomSystem.getCallsManager().getCalls()
                .iterator().next();
        assert(call.isVideoCallingSupportedByPhoneAccount());
        assertEquals(VideoProfile.STATE_BIDIRECTIONAL, call.getVideoState());

        // Change the phone account to one which supports video calling.
        call.setTargetPhoneAccount(mPhoneAccountA1.getAccountHandle());
        assert(call.isVideoCallingSupportedByPhoneAccount());
        assertEquals(VideoProfile.STATE_BIDIRECTIONAL, call.getVideoState());
    }
}
