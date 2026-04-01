class canPlaceAt {
@Override
    public boolean canPlaceAt(BlockVector3 position, com.sk89q.worldedit.world.block.BlockState blockState) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.canPlaceAt(getWorld(), position, blockState);
        }
        // We can't check, so assume yes.
        return true;
    }
}
