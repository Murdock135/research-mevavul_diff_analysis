class pushDynamicShortcut {
public boolean pushDynamicShortcut(@NonNull ShortcutInfo newShortcut,
            @NonNull List<ShortcutInfo> changedShortcuts) {
        Preconditions.checkArgument(newShortcut.isEnabled(),
                "pushDynamicShortcuts() cannot publish disabled shortcuts");

        newShortcut.addFlags(ShortcutInfo.FLAG_DYNAMIC);

        changedShortcuts.clear();
        final ShortcutInfo oldShortcut = findShortcutById(newShortcut.getId());
        boolean deleted = false;

        if (oldShortcut == null || !oldShortcut.isDynamic()) {
            final ShortcutService service = mShortcutUser.mService;
            final int maxShortcuts = service.getMaxActivityShortcuts();

            final ArrayMap<ComponentName, ArrayList<ShortcutInfo>> all =
                    sortShortcutsToActivities();
            final ArrayList<ShortcutInfo> activityShortcuts = all.get(newShortcut.getActivity());

            if (activityShortcuts != null && activityShortcuts.size() > maxShortcuts) {
                // Root cause was discovered in b/233155034, so this should not be happening.
                service.wtf("Error pushing shortcut. There are already "
                        + activityShortcuts.size() + " shortcuts.");
            }
            if (activityShortcuts != null && activityShortcuts.size() == maxShortcuts) {
                // Max has reached. Delete the shortcut with lowest rank.
                // Sort by isManifestShortcut() and getRank().
                Collections.sort(activityShortcuts, mShortcutTypeAndRankComparator);

                final ShortcutInfo shortcut = activityShortcuts.get(maxShortcuts - 1);
                if (shortcut.isManifestShortcut()) {
                    // All shortcuts are manifest shortcuts and cannot be removed.
                    Slog.e(TAG, "Failed to remove manifest shortcut while pushing dynamic shortcut "
                            + newShortcut.getId());
                    return true;  // poppedShortcuts is empty which indicates a failure.
                }

                changedShortcuts.add(shortcut);
                deleted = deleteDynamicWithId(shortcut.getId(), /* ignoreInvisible =*/ true,
                        /*ignorePersistedShortcuts=*/ true) != null;
            }
        }
        if (oldShortcut != null) {
            // It's an update case.
            // Make sure the target is updatable. (i.e. should be mutable.)
            oldShortcut.ensureUpdatableWith(newShortcut, /*isUpdating=*/ false);

            // If it was originally pinned or cached, the new one should be pinned or cached too.
            newShortcut.addFlags(oldShortcut.getFlags()
                    & (ShortcutInfo.FLAG_PINNED | ShortcutInfo.FLAG_CACHED_ALL));
        }

        if (newShortcut.isExcludedFromSurfaces(ShortcutInfo.SURFACE_LAUNCHER)) {
            if (isAppSearchEnabled()) {
                synchronized (mLock) {
                    mTransientShortcuts.put(newShortcut.getId(), newShortcut);
                }
            }
        } else {
            forceReplaceShortcutInner(newShortcut);
        }
        if (isAppSearchEnabled()) {
            runAsSystem(() -> fromAppSearch().thenAccept(session ->
                    session.reportUsage(new ReportUsageRequest.Builder(
                            getPackageName(), newShortcut.getId()).build(), mExecutor, result -> {
                                    if (!result.isSuccess()) {
                                        Slog.e(TAG, "Failed to report usage via AppSearch. "
                                                + result.getErrorMessage());
                                    }
                            })));
        }
        return deleted;
    }
}
