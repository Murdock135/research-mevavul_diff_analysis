class doStart {
@Override
    protected void doStart() {
        BayeuxServer bayeux = _oort.getBayeuxServer();

        _session.handshake();

        ServerChannel setiAllChannel = bayeux.createChannelIfAbsent(SETI_ALL_CHANNEL).getReference();
        setiAllChannel.addListener(_initialStateListener);
        _session.getChannel(SETI_ALL_CHANNEL).subscribe((channel, message) -> receiveBroadcast(message));
        _oort.observeChannel(SETI_ALL_CHANNEL);

        String setiChannelName = generateSetiChannel(_setiId);
        _session.getChannel(setiChannelName).subscribe((channel, message) -> receiveDirect(message));
        _oort.observeChannel(setiChannelName);

        _oort.addCometListener(_cometListener);

        if (_logger.isDebugEnabled()) {
            _logger.debug("{} started", this);
        }
    }
}
