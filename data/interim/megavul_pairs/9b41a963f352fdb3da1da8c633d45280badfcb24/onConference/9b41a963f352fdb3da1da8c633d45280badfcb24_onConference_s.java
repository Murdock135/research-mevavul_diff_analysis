class onConference {
@Override
        public void onConference(Connection cxn1, Connection cxn2) {
            if (((FakeConnection) cxn1).getIsConferenceCreated()) {
                // Usually, this is implemented by something in Telephony, which does a bunch of
                // radio work to conference the two connections together. Here we just short-cut
                // that and declare them conferenced.
                Conference fakeConference = new FakeConference();
                fakeConference.addConnection(cxn1);
                fakeConference.addConnection(cxn2);
                mLatestConference = fakeConference;
                addConference(fakeConference);
            } else {
                try {
                    sendSetConferenceMergeFailed(cxn1.getTelecomCallId());
                } catch (Exception e) {
                    Log.w(this, "Exception on sendSetConferenceMergeFailed: " + e.getMessage());
                }
            }
        }
}
