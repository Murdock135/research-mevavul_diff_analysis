class receiveKexInit {
protected byte[] receiveKexInit(Buffer buffer) throws Exception {
        Map<KexProposalOption, String> proposal = new EnumMap<>(KexProposalOption.class);

        byte[] seed;
        synchronized (kexState) {
            seed = receiveKexInit(buffer, proposal);
            receiveKexInit(proposal, seed);
        }

        if (log.isTraceEnabled()) {
            log.trace("receiveKexInit({}) proposal={} seed: {}",
                    this, proposal, BufferUtils.toHex(':', seed));
        }

        return seed;
    }
}
