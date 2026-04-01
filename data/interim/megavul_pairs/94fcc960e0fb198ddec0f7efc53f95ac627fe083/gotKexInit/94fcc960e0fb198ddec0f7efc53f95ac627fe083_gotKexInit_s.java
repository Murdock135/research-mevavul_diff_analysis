class gotKexInit {
private void gotKexInit(SSHPacket buf)
            throws TransportException {
        buf.rpos(buf.rpos() - 1);
        final Proposal serverProposal = new Proposal(buf);
        negotiatedAlgs = clientProposal.negotiate(serverProposal);
        log.debug("Negotiated algorithms: {}", negotiatedAlgs);
        for(AlgorithmsVerifier v: algorithmVerifiers) {
            log.debug("Trying to verify algorithms with {}", v);
            if(!v.verify(negotiatedAlgs)) {
                throw new TransportException(DisconnectReason.KEY_EXCHANGE_FAILED,
                        "Failed to verify negotiated algorithms `" + negotiatedAlgs + "`");
            }
        }
        kex = Factory.Named.Util.create(transport.getConfig().getKeyExchangeFactories(),
                                        negotiatedAlgs.getKeyExchangeAlgorithm());
        transport.setHostKeyAlgorithm(Factory.Named.Util.create(transport.getConfig().getKeyAlgorithms(),
                                      negotiatedAlgs.getSignatureAlgorithm()));

        try {
            kex.init(transport,
                     transport.getServerID(), transport.getClientID(),
                     serverProposal.getPacket().getCompactData(), clientProposal.getPacket().getCompactData());
        } catch (GeneralSecurityException e) {
            throw new TransportException(DisconnectReason.KEY_EXCHANGE_FAILED, e);
        }
    }
}
