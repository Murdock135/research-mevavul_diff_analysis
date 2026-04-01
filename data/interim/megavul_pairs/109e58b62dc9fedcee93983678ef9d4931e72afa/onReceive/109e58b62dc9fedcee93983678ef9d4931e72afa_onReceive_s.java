class onReceive {
@Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent pendingIntent = intent.getParcelableExtra(EXTRA_ACTION_INTENT);
        String actionType = intent.getStringExtra(EXTRA_ACTION_TYPE);
        if (DEBUG_ACTIONS) {
            Log.d(TAG, "Executing smart action [" + actionType + "]:" + pendingIntent.getIntent());
        }
        ActivityOptions opts = ActivityOptions.makeBasic();

        try {
            pendingIntent.send(context, 0, null, null, null, null, opts.toBundle());
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Pending intent canceled", e);
        }

        mScreenshotSmartActions.notifyScreenshotAction(
                context, intent.getStringExtra(EXTRA_ID), actionType, true,
                pendingIntent.getIntent());
    }
}
