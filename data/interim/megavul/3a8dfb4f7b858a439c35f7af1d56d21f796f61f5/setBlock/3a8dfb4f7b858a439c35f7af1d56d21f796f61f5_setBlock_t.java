class setBlock {
@Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, SideEffectSet sideEffects) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        if (worldNativeAccess != null) {
            try {
                return worldNativeAccess.setBlock(position, block, sideEffects);
            } catch (Exception e) {
                if (block instanceof BaseBlock && ((BaseBlock) block).getNbt() != null) {
                    LOGGER.warn("Tried to set a corrupt tile entity at " + position.toString()
                            + ": " + ((BaseBlock) block).getNbt(), e);
                } else {
                    LOGGER.warn("Failed to set block via adapter, falling back to generic", e);
                }
            }
        }
        Block bukkitBlock = getWorld().getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
        bukkitBlock.setBlockData(BukkitAdapter.adapt(block), sideEffects.doesApplyAny());
        return true;
    }
}
