class createQuickShareAction {
@VisibleForTesting
    Notification.Action createQuickShareAction(
            Notification.Action quickShare, String screenshotId, Uri uri, long imageTime,
            Bitmap image, UserHandle user) {
        if (quickShare == null) {
            return null;
        } else if (quickShare.actionIntent.isImmutable()) {
            Notification.Action quickShareWithUri =
                    queryQuickShareAction(screenshotId, image, user, uri);
            if (quickShareWithUri == null
                    || !quickShareWithUri.title.toString().contentEquals(quickShare.title)) {
                return null;
            }
            quickShare = quickShareWithUri;
        }

        Intent wrappedIntent = new Intent(mContext, SmartActionsReceiver.class)
                .putExtra(ScreenshotController.EXTRA_ACTION_INTENT, quickShare.actionIntent)
                .putExtra(ScreenshotController.EXTRA_ACTION_INTENT_FILLIN,
                        createFillInIntent(uri, imageTime))
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Bundle extras = quickShare.getExtras();
        String actionType = extras.getString(
                ScreenshotNotificationSmartActionsProvider.ACTION_TYPE,
                ScreenshotNotificationSmartActionsProvider.DEFAULT_ACTION_TYPE);
        addIntentExtras(screenshotId, wrappedIntent, actionType, mSmartActionsEnabled);
        PendingIntent broadcastIntent =
                PendingIntent.getBroadcast(mContext, mRandom.nextInt(), wrappedIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Action.Builder(quickShare.getIcon(), quickShare.title,
                broadcastIntent)
                .setContextual(true)
                .addExtras(extras)
                .build();
    }
}
