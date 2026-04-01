class generateTree {
@Override
    public boolean generateTree(TreeGenerator.TreeType type, EditSession editSession, BlockVector3 pt) {
        //FAWE start - allow tree commands to be undone and obey region restrictions
        return WorldEditPlugin.getInstance().getBukkitImplAdapter().generateTree(type, editSession, pt, getWorld());
        //FAWE end
    }
}
