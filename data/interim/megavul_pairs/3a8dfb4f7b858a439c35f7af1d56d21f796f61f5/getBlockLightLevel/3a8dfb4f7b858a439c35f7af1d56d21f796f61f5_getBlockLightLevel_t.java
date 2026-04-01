class getBlockLightLevel {
@Override
    public int getBlockLightLevel(BlockVector3 pt) {
        //FAWE start - safe edit region
        testCoords(pt);
        //FAWE end
        return getWorld().getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).getLightLevel();
    }
}
