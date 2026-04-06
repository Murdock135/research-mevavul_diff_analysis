class createQuickShareAction {
@VisibleForTesting
    private Notification.Action createQuickShareAction(Context context, Notification.Action action,
            Uri uri) {
        if (action == null) {
            return null;
        }
        // Populate image URI into Quick Share chip intent
        Intent sharingIntent = action.actionIntent.getIntent();
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        String subjectDate = DateFormat.getDateTimeInstance().format(new Date(mImageTime));
        String subject = String.format(SCREENSHOT_SHARE_SUBJECT_TEMPLATE, subjectDate);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // Include URI in ClipData also, so that grantPermission picks it up.
        // We don't use setData here because some apps interpret this as "to:".
        ClipData clipdata = new ClipData(new ClipDescription("content",
                new String[]{"image/png"}),
                new ClipData.Item(uri));
        sharingIntent.setClipData(clipdata);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent updatedPendingIntent = PendingIntent.getActivity(
                context, 0, sharingIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Proxy smart actions through {@link SmartActionsReceiver} for logging smart actions.
        Bundle extras = action.getExtras();
        String actionType = extras.getString(
                ScreenshotNotificationSmartActionsProvider.ACTION_TYPE,
                ScreenshotNotificationSmartActionsProvider.DEFAULT_ACTION_TYPE);
        Intent intent = new Intent(context, SmartActionsReceiver.class)
                .putExtra(ScreenshotController.EXTRA_ACTION_INTENT, updatedPendingIntent)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        addIntentExtras(mScreenshotId, intent, actionType, mSmartActionsEnabled);
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(context,
                mRandom.nextInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Action.Builder(action.getIcon(), action.title,
                broadcastIntent).setContextual(true).addExtras(extras).build();
    }
}
