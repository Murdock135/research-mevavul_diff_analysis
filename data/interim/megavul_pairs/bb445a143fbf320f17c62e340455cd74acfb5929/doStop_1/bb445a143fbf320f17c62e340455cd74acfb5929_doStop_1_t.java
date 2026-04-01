class doStop_1 {
@Override
    protected void doStop() throws Exception {
        super.doStop();

        unprotectOortChannels(_bayeux);

        _oortSession.disconnect();
        _oortSession.removeExtension(_binaryExtension);

        ServerChannel channel = _bayeux.getChannel(OORT_CLOUD_CHANNEL);
        if (channel != null) {
            channel.removeListener(_cloudListener);
        }

        _bayeux.removeListener(_allChannelsFilter);

        Extension binaryExtension = _serverBinaryExtension;
        _serverBinaryExtension = null;
        if (binaryExtension != null) {
            _bayeux.removeExtension(binaryExtension);
        }

        Extension ackExtension = _ackExtension;
        _ackExtension = null;
        if (ackExtension != null) {
            _bayeux.removeExtension(ackExtension);
        }

        _channels.clear();

        _scheduler.shutdown();

        for (ClientTransport.Factory factory : _transportFactories) {
            removeBean(factory);
        }
    }
}
