class testAutoSpeakerphoneOutgoingTransmitOnly {
@MediumTest
    @Test
    public void testAutoSpeakerphoneOutgoingTransmitOnly() throws Exception {
        // Start an incoming video call.
        IdPair ids = startAndMakeActiveOutgoingCall("650-555-1212",
                mPhoneAccountA0.getAccountHandle(), mConnectionServiceFixtureA,
                VideoProfile.STATE_TX_ENABLED, null);

        verifyAudioRoute(CallAudioState.ROUTE_SPEAKER);
    }
}
