class clearContainerBlockContents {
@Override
    public boolean clearContainerBlockContents(BlockVector3 pt) {
        checkNotNull(pt);
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            try {
                return adapter.clearContainerBlockContents(getWorld(), pt);
            } catch (Exception ignored) {
            }
        }
        if (!getBlock(pt).getBlockType().getMaterial().hasContainer()) {
            return false;
        }

        Block block = getWorld().getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        BlockState state = PaperLib.getBlockState(block, false).getState();
        if (!(state instanceof InventoryHolder)) {
            return false;
        }

        TaskManager.taskManager().sync(() -> {
            InventoryHolder chest = (InventoryHolder) state;
            Inventory inven = chest.getInventory();
            if (chest instanceof Chest) {
                inven = ((Chest) chest).getBlockInventory();
            }
            inven.clear();
            return null;
        });
        return true;
    }
}
