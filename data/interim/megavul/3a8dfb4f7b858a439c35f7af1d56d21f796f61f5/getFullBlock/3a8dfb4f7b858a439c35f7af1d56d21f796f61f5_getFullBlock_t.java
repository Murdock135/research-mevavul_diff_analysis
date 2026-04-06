class getFullBlock {
@Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.getFullBlock(BukkitAdapter.adapt(getWorld(), position));
        } else {
            return getBlock(position).toBaseBlock();
        }
    }
}
