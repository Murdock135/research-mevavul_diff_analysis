class write {
@Override
    public long write(SSHPacket payload)
            throws TransportException {
        writeLock.lock();
        try {

            if (kexer.isKexOngoing()) {
                // Only transport layer packets (1 to 49) allowed except SERVICE_REQUEST
                final Message m = Message.fromByte(payload.array()[payload.rpos()]);
                if (!m.in(1, 49) || m == Message.SERVICE_REQUEST) {
                    assert m != Message.KEXINIT;
                    kexer.waitForDone();
                }
            } else if (encoder.getSequenceNumber() == 0) // We get here every 2**32th packet
                kexer.startKex(true);

            final long seq = encoder.encode(payload);
            try {
                connInfo.out.write(payload.array(), payload.rpos(), payload.available());
                connInfo.out.flush();
            } catch (IOException ioe) {
                throw new TransportException(ioe);
            }

            return seq;

        } finally {
            writeLock.unlock();
        }
    }
}
