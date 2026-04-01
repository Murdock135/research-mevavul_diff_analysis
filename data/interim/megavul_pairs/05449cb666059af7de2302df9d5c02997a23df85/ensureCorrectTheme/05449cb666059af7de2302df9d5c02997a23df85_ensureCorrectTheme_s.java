class ensureCorrectTheme {
private void ensureCorrectTheme(Intent data) {
        String oldListLayout = data.getStringExtra(SettingsActivity.SP_FEED_LIST_LAYOUT);
        String newListLayout = mPrefs.getString(SettingsActivity.SP_FEED_LIST_LAYOUT,"0");

        if (ThemeChooser.themeRequiresRestartOfUI() || !newListLayout.equals(oldListLayout)) {
            NewsReaderListActivity.this.recreate();
        } else if (data.hasExtra(SettingsActivity.CACHE_CLEARED)) {
            resetUiAndStartSync();
        }
    }
}
