class getOrCreateChunk {
@Override
    public final IQueueChunk getOrCreateChunk(int x, int z) {
        getChunkLock.lock();
        try {
            final long pair = (long) x << 32 | z & 0xffffffffL;
            if (pair == lastPair) {
                return lastChunk;
            }
            if (!processGet(x, z) || (Settings.settings().REGION_RESTRICTIONS_OPTIONS.RESTRICT_TO_SAFE_RANGE
                    // if any chunk coord is outside 30 million blocks
                    && (x > 1875000 || z > 1875000 || x < -1875000 || z < -1875000))) {
                lastPair = pair;
                lastChunk = NullChunk.getInstance();
                return NullChunk.getInstance();
            }
            IQueueChunk chunk = chunks.get(pair);
            if (chunk != null) {
                lastPair = pair;
                lastChunk = chunk;
                return chunk;
            }
            final int size = chunks.size();
            final boolean lowMem = MemUtil.isMemoryLimited();
            // If queueing is enabled AND either of the following
            //  - memory is low & queue size > num threads + 8
            //  - queue size > target size and primary queue has less than num threads submissions
            int targetSize = lowMem ? Settings.settings().QUEUE.PARALLEL_THREADS + 8 : Settings.settings().QUEUE.TARGET_SIZE;
            if (enabledQueue && size > targetSize && (lowMem || Fawe.instance().getQueueHandler().isUnderutilized())) {
                chunk = chunks.removeFirst();
                final Future future = submitUnchecked(chunk);
                if (future != null && !future.isDone()) {
                    pollSubmissions(targetSize, lowMem);
                    submissions.add(future);
                }
            }
            chunk = poolOrCreate(x, z);
            chunk = wrap(chunk);

            chunks.put(pair, chunk);
            lastPair = pair;
            lastChunk = chunk;

            return chunk;
        } finally {
            getChunkLock.unlock();
        }
    }
}
