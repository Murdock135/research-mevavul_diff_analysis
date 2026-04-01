class forceReplaceShortcutInner {
private void forceReplaceShortcutInner(@NonNull ShortcutInfo newShortcut) {
        final ShortcutService s = mShortcutUser.mService;

        forceDeleteShortcutInner(newShortcut.getId());

        // Extract Icon and update the icon res ID and the bitmap path.
        s.saveIconAndFixUpShortcutLocked(this, newShortcut);
        s.fixUpShortcutResourceNamesAndValues(newShortcut);
        ensureShortcutCountBeforePush();
        saveShortcut(newShortcut);
    }
}
