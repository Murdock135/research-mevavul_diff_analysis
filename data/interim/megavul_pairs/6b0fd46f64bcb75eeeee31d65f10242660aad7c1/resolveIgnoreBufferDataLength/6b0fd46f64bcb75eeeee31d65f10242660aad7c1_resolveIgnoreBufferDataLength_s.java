class resolveIgnoreBufferDataLength {
protected int resolveIgnoreBufferDataLength() {
        if ((ignorePacketDataLength <= 0)
                || (ignorePacketsFrequency <= 0L)
                || (ignorePacketsVariance < 0)) {
            return 0;
        }

        long count = ignorePacketsCount.decrementAndGet();
        if (count > 0L) {
            return 0;
        }

        synchronized (random) {
            count = calculateNextIgnorePacketCount(
                    random, ignorePacketsFrequency, ignorePacketsVariance);
            ignorePacketsCount.set(count);
            return ignorePacketDataLength + random.random(ignorePacketDataLength);
        }
    }
}
