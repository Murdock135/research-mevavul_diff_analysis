class useItem {
@Override
    public boolean useItem(BlockVector3 position, BaseItem item, Direction face) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.simulateItemUse(getWorld(), position, item, face);
        }

        return false;
    }
}
