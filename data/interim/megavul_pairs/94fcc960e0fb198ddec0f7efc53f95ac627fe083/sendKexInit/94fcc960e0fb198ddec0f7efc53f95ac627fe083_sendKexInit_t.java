class sendKexInit {
private void sendKexInit()
            throws TransportException {
        log.debug("Sending SSH_MSG_KEXINIT");
        List<String> knownHostAlgs = findKnownHostAlgs(transport.getRemoteHost(), transport.getRemotePort());
        clientProposal = new Proposal(transport.getConfig(), knownHostAlgs, initialKex.get());
        transport.write(clientProposal.getPacket());
        kexInitSent.set();
    }
}
