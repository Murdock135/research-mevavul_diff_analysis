class sendNewKeys {
private void sendNewKeys()
            throws TransportException {
        log.debug("Sending SSH_MSG_NEWKEYS");
        transport.write(new SSHPacket(Message.NEWKEYS));
    }
}
