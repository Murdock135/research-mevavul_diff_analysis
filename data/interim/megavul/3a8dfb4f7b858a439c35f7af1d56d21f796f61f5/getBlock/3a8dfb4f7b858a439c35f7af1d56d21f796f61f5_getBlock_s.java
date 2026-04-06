class getBlock {
@Override
    public com.sk89q.worldedit.world.block.BlockState getBlock(BlockVector3 position) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            try {
                return adapter.getBlock(BukkitAdapter.adapt(getWorld(), position)).toImmutableState();
            } catch (Exception e) {
                if (!hasWarnedImplError) {
                    hasWarnedImplError = true;
                    LOGGER.warn("Unable to retrieve block via impl adapter", e);
                }
            }
        }
        if (WorldEditPlugin.getInstance().getLocalConfiguration().unsupportedVersionEditing) {
            Block bukkitBlock = getWorld().getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            return BukkitAdapter.adapt(bukkitBlock.getBlockData());
        } else {
            throw new RuntimeException(new UnsupportedVersionEditException());
        }
    }
}
