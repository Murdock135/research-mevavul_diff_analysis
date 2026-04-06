class get {
@Override
    public IChunkGet get(int chunkX, int chunkZ) {
        testCoords(BlockVector3.at(chunkX << 16, 0, chunkZ << 16));
        return WorldEditPlugin.getInstance().getBukkitImplAdapter().get(getWorldChecked(), chunkX, chunkZ);
    }
}
