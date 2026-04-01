class getBiome {
@SuppressWarnings("deprecation")
    @Override
    public BiomeType getBiome(BlockVector3 position) {
        if (HAS_3D_BIOMES) {
            return BukkitAdapter.adapt(getWorld().getBiome(position.getBlockX(), position.getBlockY(), position.getBlockZ()));
        } else {
            return BukkitAdapter.adapt(getWorld().getBiome(position.getBlockX(), position.getBlockZ()));
        }
    }
}
