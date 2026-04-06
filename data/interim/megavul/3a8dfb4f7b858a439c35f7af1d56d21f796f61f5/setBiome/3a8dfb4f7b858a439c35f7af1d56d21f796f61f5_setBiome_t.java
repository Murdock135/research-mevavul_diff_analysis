class setBiome {
@SuppressWarnings("deprecation")
    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        if (HAS_3D_BIOMES) {
            getWorld().setBiome(position.getBlockX(), position.getBlockY(), position.getBlockZ(), BukkitAdapter.adapt(biome));
        } else {
            getWorld().setBiome(position.getBlockX(), position.getBlockZ(), BukkitAdapter.adapt(biome));
        }
        return true;
    }
}
