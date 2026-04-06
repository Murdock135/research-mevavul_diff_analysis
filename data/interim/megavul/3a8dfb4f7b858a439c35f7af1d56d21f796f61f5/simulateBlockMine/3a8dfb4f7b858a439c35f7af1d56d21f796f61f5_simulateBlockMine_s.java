class simulateBlockMine {
@Override
    public void simulateBlockMine(BlockVector3 pt) {
        getWorld().getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).breakNaturally();
    }
}
