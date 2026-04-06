class onSurfaceShownChanged {
void onSurfaceShownChanged(boolean shown) {
        if (mLastShownChangedReported == shown) {
            return;
        }
        mLastShownChangedReported = shown;

        if (shown) {
            initExclusionRestrictions();
        } else {
            logExclusionRestrictions(EXCLUSION_LEFT);
            logExclusionRestrictions(EXCLUSION_RIGHT);
        }
        // Exclude toast because legacy apps may show toast window by themselves, so the misused
        // apps won't always be considered as foreground state.
        // Exclude private presentations as they can only be shown on private virtual displays and
        // shouldn't be the cause of an app be considered foreground.
        // Exclude presentations on virtual displays as they are not actually visible.
        if (mAttrs.type >= FIRST_SYSTEM_WINDOW
                && mAttrs.type != TYPE_TOAST
                && mAttrs.type != TYPE_PRIVATE_PRESENTATION
                && !(mAttrs.type == TYPE_PRESENTATION && isOnVirtualDisplay())
        ) {
            mWmService.mAtmService.mActiveUids.onNonAppSurfaceVisibilityChanged(mOwnerUid, shown);
        }
    }
}
