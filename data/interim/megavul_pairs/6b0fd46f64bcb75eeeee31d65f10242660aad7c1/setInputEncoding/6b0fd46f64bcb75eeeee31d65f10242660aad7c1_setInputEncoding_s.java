class setInputEncoding {
protected void setInputEncoding() throws Exception {
        inCipher = inSettings.getCipher(seqi);
        inMac = inSettings.getMac();
        inCompression = inSettings.getCompression();
        inSettings = null;
        inCipherSize = inCipher.getCipherBlockSize();
        inMacSize = inMac != null ? inMac.getBlockSize() : 0;
        inMacResult = new byte[inMacSize];
        // TODO add support for configurable compression level
        inCompression.init(Compression.Type.Inflater, -1);

        maxRekeyBlocks.set(determineRekeyBlockLimit(inCipherSize, outCipherSize));

        inBytesCount.set(0L);
        inPacketsCount.set(0L);
        inBlocksCount.set(0L);

        lastKeyTimeValue.set(Instant.now());
        firstKexPacketFollows = null;

        if (log.isDebugEnabled()) {
            log.debug("setInputEncoding({}): cipher {}; mac {}; compression {}; blocks limit {}", this, inCipher, inMac,
                    inCompression, maxRekeyBlocks);
        }
    }
}
