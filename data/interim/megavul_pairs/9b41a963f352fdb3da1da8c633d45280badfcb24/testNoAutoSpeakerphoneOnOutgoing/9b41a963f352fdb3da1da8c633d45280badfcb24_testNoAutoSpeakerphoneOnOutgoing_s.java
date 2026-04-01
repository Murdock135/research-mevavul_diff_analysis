class testNoAutoSpeakerphoneOnOutgoing {
@MediumTest
    @Test
    public void testNoAutoSpeakerphoneOnOutgoing() throws Exception {
        // Start an incoming video call.
        IdPair ids = startAndMakeActiveOutgoingCall("650-555-1212",
                mPhoneAccountA0.getAccountHandle(), mConnectionServiceFixtureA,
                VideoProfile.STATE_AUDIO_ONLY);

        verifyAudioRoute(CallAudioState.ROUTE_EARPIECE);
    }
}
