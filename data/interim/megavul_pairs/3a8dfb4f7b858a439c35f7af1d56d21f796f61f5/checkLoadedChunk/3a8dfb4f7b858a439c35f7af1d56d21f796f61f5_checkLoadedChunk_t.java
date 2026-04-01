class checkLoadedChunk {
@Override
    public void checkLoadedChunk(BlockVector3 pt) {
        //FAWE start - safe edit region
        testCoords(pt);
        //FAWE end
        World world = getWorld();
        //FAWE start
        int X = pt.getBlockX() >> 4;
        int Z = pt.getBlockZ() >> 4;
        if (Fawe.isMainThread()) {
            world.getChunkAt(X, Z);
        } else if (PaperLib.isPaper()) {
            PaperLib.getChunkAtAsync(world, X, Z, true);
        }
        //FAWE end
    }
}
