class simulateBlockMine {
@Override
    public void simulateBlockMine(BlockVector3 pt) {
        //FAWE start - safe edit region
        testCoords(pt);
        //FAWE end
        getWorld().getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).breakNaturally();
    }
}
