class params {
public ConnectionParams params(ExecutorService consumerWorkServiceExecutor) {
        ConnectionParams result = new ConnectionParams();

        result.setCredentialsProvider(credentialsProvider);
        result.setConsumerWorkServiceExecutor(consumerWorkServiceExecutor);
        result.setVirtualHost(virtualHost);
        result.setClientProperties(getClientProperties());
        result.setRequestedFrameMax(requestedFrameMax);
        result.setRequestedChannelMax(requestedChannelMax);
        result.setShutdownTimeout(shutdownTimeout);
        result.setSaslConfig(saslConfig);
        result.setNetworkRecoveryInterval(networkRecoveryInterval);
        result.setRecoveryDelayHandler(recoveryDelayHandler);
        result.setTopologyRecovery(topologyRecovery);
        result.setTopologyRecoveryExecutor(topologyRecoveryExecutor);
        result.setExceptionHandler(exceptionHandler);
        result.setThreadFactory(threadFactory);
        result.setHandshakeTimeout(handshakeTimeout);
        result.setRequestedHeartbeat(requestedHeartbeat);
        result.setShutdownExecutor(shutdownExecutor);
        result.setHeartbeatExecutor(heartbeatExecutor);
        result.setChannelRpcTimeout(channelRpcTimeout);
        result.setChannelShouldCheckRpcResponseType(channelShouldCheckRpcResponseType);
        result.setWorkPoolTimeout(workPoolTimeout);
        result.setErrorOnWriteListener(errorOnWriteListener);
        result.setTopologyRecoveryFilter(topologyRecoveryFilter);
        result.setConnectionRecoveryTriggeringCondition(connectionRecoveryTriggeringCondition);
        result.setTopologyRecoveryRetryHandler(topologyRecoveryRetryHandler);
        result.setRecoveredQueueNameSupplier(recoveredQueueNameSupplier);
        result.setTrafficListener(trafficListener);
        result.setCredentialsRefreshService(credentialsRefreshService);
        result.setMaxInboundMessageBodySize(maxInboundMessageBodySize);
        return result;
    }
}
