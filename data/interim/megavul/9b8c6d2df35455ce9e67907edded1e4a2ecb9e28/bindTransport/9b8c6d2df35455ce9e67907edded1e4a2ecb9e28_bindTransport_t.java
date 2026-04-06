class bindTransport {
boolean bindTransport(ServiceInfo transport) {
        ComponentName svcName = new ComponentName(transport.packageName, transport.name);
        if (!mTransportWhitelist.contains(svcName)) {
            Slog.w(TAG, "Proposed transport " + svcName + " not whitelisted; ignoring");
            return false;
        }

        if (DEBUG) {
            Slog.i(TAG, "Binding to transport host " + svcName);
        }
        Intent intent = new Intent(mTransportServiceIntent);
        intent.setComponent(svcName);

        TransportConnection connection;
        synchronized (mTransports) {
            connection = mTransportConnections.get(transport.packageName);
            if (null == connection) {
                connection = new TransportConnection(transport);
                mTransportConnections.put(transport.packageName, connection);
            } else {
                // This is a rebind due to package upgrade.  The service won't be
                // automatically relaunched for us until we explicitly rebind, but
                // we need to unbind the now-orphaned original connection.
                mContext.unbindService(connection);
            }
        }
        return mContext.bindServiceAsUser(intent,
                connection, Context.BIND_AUTO_CREATE,
                UserHandle.OWNER);
    }
}
