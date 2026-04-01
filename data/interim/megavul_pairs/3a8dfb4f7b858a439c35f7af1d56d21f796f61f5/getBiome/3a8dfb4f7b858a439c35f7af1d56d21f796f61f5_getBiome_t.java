class getBiome {
@SuppressWarnings("deprecation")
    @Override
    public BiomeType getBiome(BlockVector3 position) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        if (HAS_3D_BIOMES) {
            return BukkitAdapter.adapt(getWorld().getBiome(position.getBlockX(), position.getBlockY(), position.getBlockZ()));
        } else {
            return BukkitAdapter.adapt(getWorld().getBiome(position.getBlockX(), position.getBlockZ()));
        }
    }
}
