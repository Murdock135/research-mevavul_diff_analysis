class sendKexInit {
protected byte[] sendKexInit() throws Exception {
        Map<KexProposalOption, String> proposal = doStrictKexProposal(getKexProposal());

        byte[] seed;
        synchronized (kexState) {
            DefaultKeyExchangeFuture initFuture = kexInitializedFuture;
            if (initFuture == null) {
                initFuture = new DefaultKeyExchangeFuture(toString(), null);
                kexInitializedFuture = initFuture;
            }
            try {
                seed = sendKexInit(proposal);
                setKexSeed(seed);
                initFuture.setValue(Boolean.TRUE);
            } catch (Exception e) {
                initFuture.setValue(e);
                throw e;
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("sendKexInit({}) proposal={} seed: {}", this, proposal, BufferUtils.toHex(':', seed));
        }
        return seed;
    }
}
