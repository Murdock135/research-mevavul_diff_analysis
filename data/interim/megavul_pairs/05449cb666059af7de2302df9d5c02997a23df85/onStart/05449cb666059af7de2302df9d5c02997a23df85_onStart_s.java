class onStart {
@Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        intent.putExtra(
                SettingsActivity.SP_FEED_LIST_LAYOUT,
                mPrefs.getString(SettingsActivity.SP_FEED_LIST_LAYOUT, "0")
        );
        setResult(RESULT_OK,intent);
    }
}
