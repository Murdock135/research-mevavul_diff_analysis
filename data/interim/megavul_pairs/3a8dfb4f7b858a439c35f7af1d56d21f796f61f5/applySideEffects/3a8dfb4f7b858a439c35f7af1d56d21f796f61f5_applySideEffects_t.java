class applySideEffects {
@Override
    public Set<SideEffect> applySideEffects(
            BlockVector3 position, com.sk89q.worldedit.world.block.BlockState previousType,
            SideEffectSet sideEffectSet
    ) {
        //FAWE start - safe edit region
        testCoords(position);
        //FAWE end
        if (worldNativeAccess != null) {
            worldNativeAccess.applySideEffects(position, previousType, sideEffectSet);
            return Sets.intersection(
                    WorldEditPlugin.getInstance().getInternalPlatform().getSupportedSideEffects(),
                    sideEffectSet.getSideEffectsToApply()
            );
        }

        return ImmutableSet.of();
    }
}
