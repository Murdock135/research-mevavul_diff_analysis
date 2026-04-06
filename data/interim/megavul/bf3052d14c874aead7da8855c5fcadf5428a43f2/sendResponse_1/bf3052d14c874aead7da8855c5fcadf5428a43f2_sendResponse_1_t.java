class sendResponse_1 {
@Override
    public void sendResponse(Throwable error) throws IOException {
        BytesStreamOutput stream = new BytesStreamOutput();
        if (ThrowableObjectOutputStream.canSerialize(error) == false) {
            assert false : "Can not serialize exception: " + error; // make sure tests fail
            error = new NotSerializableTransportException(error);
        }
        try {
            writeResponseExceptionHeader(stream);
            RemoteTransportException tx = new RemoteTransportException(targetTransport.nodeName(), targetTransport.boundAddress().boundAddress(), action, error);
            ThrowableObjectOutputStream too = new ThrowableObjectOutputStream(stream);
            too.writeObject(tx);
            too.close();
        } catch (NotSerializableException e) {
            stream.reset();
            writeResponseExceptionHeader(stream);
            RemoteTransportException tx = new RemoteTransportException(targetTransport.nodeName(), targetTransport.boundAddress().boundAddress(), action, new NotSerializableTransportException(error));
            ThrowableObjectOutputStream too = new ThrowableObjectOutputStream(stream);
            too.writeObject(tx);
            too.close();
        }
        final byte[] data = stream.bytes().toBytes();
        targetTransport.workers().execute(new Runnable() {
            @Override
            public void run() {
                targetTransport.messageReceived(data, action, sourceTransport, version, null);
            }
        });
        sourceTransportServiceAdapter.onResponseSent(requestId, action, error);
    }
}
