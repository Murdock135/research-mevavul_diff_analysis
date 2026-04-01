class testAutoSpeakerphoneOutgoingBidirectional {
@MediumTest
    @Test
    public void testAutoSpeakerphoneOutgoingBidirectional() throws Exception {
        // Start an incoming video call.
        IdPair ids = startAndMakeActiveOutgoingCall("650-555-1212",
                mPhoneAccountA0.getAccountHandle(), mConnectionServiceFixtureA,
                VideoProfile.STATE_BIDIRECTIONAL);

        verifyAudioRoute(CallAudioState.ROUTE_SPEAKER);
    }
}
