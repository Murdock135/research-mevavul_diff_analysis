class testTelecomManagerAcceptRingingVideoCallAsAudio {
@LargeTest
    @Test
    public void testTelecomManagerAcceptRingingVideoCallAsAudio() throws Exception {
        IdPair ids = startIncomingPhoneCall("650-555-1212", mPhoneAccountA0.getAccountHandle(),
                VideoProfile.STATE_BIDIRECTIONAL, mConnectionServiceFixtureA);

        assertEquals(Call.STATE_RINGING, mInCallServiceFixtureX.getCall(ids.mCallId).getState());
        assertEquals(Call.STATE_RINGING, mInCallServiceFixtureY.getCall(ids.mCallId).getState());

        // Use TelecomManager API to answer the ringing call.
        TelecomManager telecomManager = (TelecomManager) mComponentContextFixture.getTestDouble()
                .getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
        telecomManager.acceptRingingCall(VideoProfile.STATE_AUDIO_ONLY);

        waitForHandlerAction(mTelecomSystem.getCallsManager()
                .getConnectionServiceFocusManager().getHandler(), TEST_TIMEOUT);

        // The generic answer method on the ConnectionService is used to answer audio-only calls.
        verify(mConnectionServiceFixtureA.getTestDouble(), timeout(TEST_TIMEOUT))
                .answer(eq(ids.mConnectionId), any());
        mConnectionServiceFixtureA.sendSetActive(ids.mConnectionId);

        mInCallServiceFixtureX.mInCallAdapter.disconnectCall(ids.mCallId);
    }
}
