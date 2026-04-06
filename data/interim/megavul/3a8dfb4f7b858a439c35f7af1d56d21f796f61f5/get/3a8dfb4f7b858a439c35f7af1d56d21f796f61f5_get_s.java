class get {
@Override
    public IChunkGet get(int chunkX, int chunkZ) {
        return WorldEditPlugin.getInstance().getBukkitImplAdapter().get(getWorldChecked(), chunkX, chunkZ);
    }
}
