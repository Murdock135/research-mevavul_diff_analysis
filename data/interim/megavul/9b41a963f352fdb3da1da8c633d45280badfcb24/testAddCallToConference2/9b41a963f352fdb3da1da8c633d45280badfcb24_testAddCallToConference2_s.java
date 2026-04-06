class testAddCallToConference2 {
@MediumTest
    @Test
    public void testAddCallToConference2() throws Exception {
        ParcelableCall conferenceCall = makeConferenceCall();
        IdPair callId3 = startAndMakeActiveOutgoingCall("650-555-1214",
                mPhoneAccountA0.getAccountHandle(), mConnectionServiceFixtureA);
        mInCallServiceFixtureX.getInCallAdapter()
                .conference(callId3.mCallId, conferenceCall.getId());
        Thread.sleep(200);

        ParcelableCall call3 = mInCallServiceFixtureX.getCall(callId3.mCallId);
        ParcelableCall updatedConference = mInCallServiceFixtureX.getCall(conferenceCall.getId());
        assertEquals(conferenceCall.getId(), call3.getParentCallId());
        assertEquals(3, updatedConference.getChildCallIds().size());
        assertTrue(updatedConference.getChildCallIds().contains(callId3.mCallId));
    }
}
