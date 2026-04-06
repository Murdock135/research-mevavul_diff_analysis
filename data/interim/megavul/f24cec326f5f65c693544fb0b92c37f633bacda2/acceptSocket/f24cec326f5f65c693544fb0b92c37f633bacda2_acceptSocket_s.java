class acceptSocket {
private BluetoothSocket acceptSocket(String RemoteAddr) throws IOException {
        BluetoothSocket as = new BluetoothSocket(this);
        as.mSocketState = SocketState.CONNECTED;
        FileDescriptor[] fds = mSocket.getAncillaryFileDescriptors();
        if (DBG) Log.d(TAG, "socket fd passed by stack  fds: " + fds);
        if(fds == null || fds.length != 1) {
            Log.e(TAG, "socket fd passed from stack failed, fds: " + fds);
            as.close();
            throw new IOException("bt socket acept failed");
        }
        as.mSocket = new LocalSocket(fds[0]);
        try {
            as.mSocket.closeExternalFd();
        } catch (IOException e) {
            Log.e(TAG, "closeExternalFd failed");
        }
        as.mSocketIS = as.mSocket.getInputStream();
        as.mSocketOS = as.mSocket.getOutputStream();
        as.mAddress = RemoteAddr;
        as.mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(RemoteAddr);
        as.mPort = mPort;
        return as;
    }
}
