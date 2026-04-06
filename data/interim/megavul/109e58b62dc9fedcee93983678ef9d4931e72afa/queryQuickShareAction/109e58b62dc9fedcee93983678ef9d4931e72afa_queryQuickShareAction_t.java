class queryQuickShareAction {
@VisibleForTesting
    Notification.Action queryQuickShareAction(
            String screenshotId, Bitmap image, UserHandle user, Uri uri) {
        CompletableFuture<List<Notification.Action>> quickShareActionsFuture =
                mScreenshotSmartActions.getSmartActionsFuture(
                        screenshotId, uri, image, mSmartActionsProvider, QUICK_SHARE_ACTION,
                        mSmartActionsEnabled, user);
        int timeoutMs = DeviceConfig.getInt(
                DeviceConfig.NAMESPACE_SYSTEMUI,
                SystemUiDeviceConfigFlags.SCREENSHOT_NOTIFICATION_QUICK_SHARE_ACTIONS_TIMEOUT_MS,
                500);
        List<Notification.Action> quickShareActions =
                mScreenshotSmartActions.getSmartActions(
                        screenshotId, quickShareActionsFuture, timeoutMs,
                        mSmartActionsProvider, QUICK_SHARE_ACTION);
        if (!quickShareActions.isEmpty()) {
            return quickShareActions.get(0);
        }
        return null;
    }
}
