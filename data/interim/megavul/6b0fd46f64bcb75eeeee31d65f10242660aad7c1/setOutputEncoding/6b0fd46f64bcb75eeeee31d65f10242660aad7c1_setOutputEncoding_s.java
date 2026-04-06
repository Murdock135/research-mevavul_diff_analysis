class setOutputEncoding {
protected void setOutputEncoding() throws Exception {
        outCipher = outSettings.getCipher(seqo);
        outMac = outSettings.getMac();
        outCompression = outSettings.getCompression();
        outSettings = null;
        outCipherSize = outCipher.getCipherBlockSize();
        outMacSize = outMac != null ? outMac.getBlockSize() : 0;
        // TODO add support for configurable compression level
        outCompression.init(Compression.Type.Deflater, -1);

        maxRekeyBlocks.set(determineRekeyBlockLimit(inCipherSize, outCipherSize));

        outBytesCount.set(0L);
        outPacketsCount.set(0L);
        outBlocksCount.set(0L);

        lastKeyTimeValue.set(Instant.now());
        firstKexPacketFollows = null;

        if (log.isDebugEnabled()) {
            log.debug("setOutputEncoding({}): cipher {}; mac {}; compression {}; blocks limit {}", this, outCipher, outMac,
                    outCompression, maxRekeyBlocks);
        }
    }
}
