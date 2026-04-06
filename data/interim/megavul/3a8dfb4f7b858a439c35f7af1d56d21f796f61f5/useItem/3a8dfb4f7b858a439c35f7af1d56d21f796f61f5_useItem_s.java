class useItem {
@Override
    public boolean useItem(BlockVector3 position, BaseItem item, Direction face) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.simulateItemUse(getWorld(), position, item, face);
        }

        return false;
    }
}
