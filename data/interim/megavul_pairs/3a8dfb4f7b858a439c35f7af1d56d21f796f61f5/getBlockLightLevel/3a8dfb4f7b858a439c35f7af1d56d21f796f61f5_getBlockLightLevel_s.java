class getBlockLightLevel {
@Override
    public int getBlockLightLevel(BlockVector3 pt) {
        return getWorld().getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).getLightLevel();
    }
}
