class doStart_1 {
@Override
    protected void doStart() throws Exception {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        scheduler.setRemoveOnCancelPolicy(true);
        _scheduler = scheduler;

        if (_transportFactories.isEmpty()) {
            _transportFactories.add(new WebSocketTransport.Factory());
            _transportFactories.add(new JettyHttpClientTransport.Factory(new HttpClient()));
        }
        for (ClientTransport.Factory factory : _transportFactories) {
            addBean(factory);
        }

        if (isAckExtensionEnabled()) {
            boolean present = false;
            for (Extension extension : _bayeux.getExtensions()) {
                if (extension instanceof AcknowledgedMessagesExtension) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                _bayeux.addExtension(_ackExtension = new AcknowledgedMessagesExtension());
            }
        }

        if (isBinaryExtensionEnabled()) {
            _oortSession.addExtension(_binaryExtension = new org.cometd.client.ext.BinaryExtension());
            boolean present = false;
            for (Extension extension : _bayeux.getExtensions()) {
                if (extension instanceof BinaryExtension) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                _bayeux.addExtension(_serverBinaryExtension = new BinaryExtension());
            }
        }

        _bayeux.addListener(_allChannelsFilter);

        ServerChannel oortCloudChannel = _bayeux.createChannelIfAbsent(OORT_CLOUD_CHANNEL).getReference();
        oortCloudChannel.addListener(_cloudListener);

        _oortSession.handshake();

        protectOortChannels(_bayeux);

        super.doStart();
    }
}
