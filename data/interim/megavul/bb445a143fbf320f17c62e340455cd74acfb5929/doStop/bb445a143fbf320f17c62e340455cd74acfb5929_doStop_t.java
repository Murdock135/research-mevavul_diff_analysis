class doStop {
@Override
    protected void doStop() {
        BayeuxServer bayeux = _oort.getBayeuxServer();

        removeAssociationsAndPresences();
        _presenceListeners.clear();

        _oort.removeCometListener(_cometListener);

        String setiChannelName = generateSetiChannel(_setiId);
        _oort.deobserveChannel(setiChannelName);

        _oort.deobserveChannel(SETI_ALL_CHANNEL);
        ServerChannel setiAllChannel = bayeux.getChannel(SETI_ALL_CHANNEL);
        if (setiAllChannel != null) {
            setiAllChannel.removeListener(_initialStateListener);
        }

        unprotectSetiChannels(bayeux);

        _session.disconnect();

        bayeux.removeListener(_allChannelsFilter);
    }
}
