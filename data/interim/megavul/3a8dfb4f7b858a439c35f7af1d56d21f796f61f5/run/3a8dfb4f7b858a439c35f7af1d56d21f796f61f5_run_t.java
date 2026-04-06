class run {
@Override
    public void run() {
        FaweTimer timer = Fawe.instance().getTimer();
        if (cancelled.get()) {
            return;
        }
        if (update.isEmpty()) {
            TaskManager.taskManager().laterAsync(this, 1);
            return;
        }
        Iterator<Map.Entry<UUID, MutablePair<World, Set<BlockVector2>>>> plrIter = update.entrySet().iterator();
        while (timer.getTPS() > 18 && plrIter.hasNext()) {
            if (cancelled.get()) {
                return;
            }
            Map.Entry<UUID, MutablePair<World, Set<BlockVector2>>> entry = plrIter.next();
            MutablePair<World, Set<BlockVector2>> pair = entry.getValue();
            World world = pair.getKey();
            Set<BlockVector2> chunks = pair.getValue();
            if (chunks != null) {
                Iterator<BlockVector2> chunksIter = chunks.iterator();
                while (chunksIter.hasNext() && pair.getValue() == chunks) { // Ensure the queued load is still valid
                    BlockVector2 chunk = chunksIter.next();
                    if (Settings.settings().REGION_RESTRICTIONS_OPTIONS.RESTRICT_TO_SAFE_RANGE) {
                        int x = chunk.getX();
                        int z = chunk.getZ();
                        // if any chunk coord is outside 30 million blocks
                        if (x > 1875000 || z > 1875000 || x < -1875000 || z < -1875000) {
                            continue;
                        }
                    }
                    queueLoad(world, chunk);
                }
            }
            plrIter.remove();
        }
        if (cancelled.get()) {
            return;
        }
        TaskManager.taskManager().laterAsync(this, 20);
    }
}
