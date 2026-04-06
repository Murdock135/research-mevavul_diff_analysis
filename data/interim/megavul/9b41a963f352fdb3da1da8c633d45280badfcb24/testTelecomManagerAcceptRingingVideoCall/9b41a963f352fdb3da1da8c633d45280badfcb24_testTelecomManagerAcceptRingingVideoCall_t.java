class testTelecomManagerAcceptRingingVideoCall {
@LargeTest
    @Test
    public void testTelecomManagerAcceptRingingVideoCall() throws Exception {
        IdPair ids = startIncomingPhoneCall("650-555-1212", mPhoneAccountA0.getAccountHandle(),
                VideoProfile.STATE_BIDIRECTIONAL, mConnectionServiceFixtureA, null);

        assertEquals(Call.STATE_RINGING, mInCallServiceFixtureX.getCall(ids.mCallId).getState());
        assertEquals(Call.STATE_RINGING, mInCallServiceFixtureY.getCall(ids.mCallId).getState());

        // Use TelecomManager API to answer the ringing call; the default expected behavior is to
        // answer using whatever video state the ringing call requests.
        TelecomManager telecomManager = (TelecomManager) mComponentContextFixture.getTestDouble()
                .getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
        telecomManager.acceptRingingCall();

        // Answer video API should be called
        verify(mConnectionServiceFixtureA.getTestDouble(), timeout(TEST_TIMEOUT))
                .answerVideo(eq(ids.mConnectionId), eq(VideoProfile.STATE_BIDIRECTIONAL), any());
        mConnectionServiceFixtureA.sendSetActive(ids.mConnectionId);

        mInCallServiceFixtureX.mInCallAdapter.disconnectCall(ids.mCallId);
    }
}
