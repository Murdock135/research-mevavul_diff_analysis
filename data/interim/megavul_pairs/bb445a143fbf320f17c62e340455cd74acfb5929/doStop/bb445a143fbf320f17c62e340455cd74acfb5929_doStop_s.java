class doStop {
@Override
    protected void doStop() {
        removeAssociationsAndPresences();
        _presenceListeners.clear();

        _session.disconnect();

        _oort.removeCometListener(_cometListener);

        String setiChannelName = generateSetiChannel(_setiId);
        _oort.deobserveChannel(setiChannelName);

        _oort.deobserveChannel(SETI_ALL_CHANNEL);
        ServerChannel setiAllChannel = _oort.getBayeuxServer().getChannel(SETI_ALL_CHANNEL);
        if (setiAllChannel != null) {
            setiAllChannel.removeListener(_initialStateListener);
        }
    }
}
