class refreshChunk {
@Override
    public void refreshChunk(int chunkX, int chunkZ) {
        testCoords(BlockVector3.at(chunkX << 16, 0, chunkZ << 16));
        getWorld().refreshChunk(chunkX, chunkZ);
    }
}
