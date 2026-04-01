class onStart {
@Override
    protected void onStart() {
        super.onStart();

        // Fix GHSL-2021-1033
        Intent intent = new Intent();
        String feedListLayout = mPrefs.getString(SettingsActivity.SP_FEED_LIST_LAYOUT, "0");
        intent.putExtra(SettingsActivity.AI_FEED_LIST_LAYOUT, feedListLayout);
        setResult(RESULT_OK, intent);
    }
}
