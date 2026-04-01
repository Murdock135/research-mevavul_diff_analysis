class parcelable {
private ParcelableConnection parcelable(ConnectionInfo c) {
        return new ParcelableConnection(
                c.request.getAccountHandle(),
                c.state,
                c.capabilities,
                c.properties,
                c.supportedAudioRoutes,
                c.request.getAddress(),
                c.addressPresentation,
                c.callerDisplayName,
                c.callerDisplayNamePresentation,
                c.videoProvider,
                c.videoState,
                false, /* ringback requested */
                false, /* voip audio mode */
                0, /* Connect Time for conf call on this connection */
                0, /* Connect Real Time comes from conference call */
                c.statusHints,
                c.disconnectCause,
                c.conferenceableConnectionIds,
                c.extras,
                c.callerNumberVerificationStatus);
    }
}
