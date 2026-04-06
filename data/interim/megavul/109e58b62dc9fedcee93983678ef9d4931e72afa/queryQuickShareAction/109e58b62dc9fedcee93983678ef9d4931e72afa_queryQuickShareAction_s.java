class queryQuickShareAction {
private void queryQuickShareAction(Bitmap image, UserHandle user) {
        CompletableFuture<List<Notification.Action>> quickShareActionsFuture =
                mScreenshotSmartActions.getSmartActionsFuture(
                        mScreenshotId, null, image, mSmartActionsProvider,
                        QUICK_SHARE_ACTION,
                        mSmartActionsEnabled, user);
        int timeoutMs = DeviceConfig.getInt(
                DeviceConfig.NAMESPACE_SYSTEMUI,
                SystemUiDeviceConfigFlags.SCREENSHOT_NOTIFICATION_QUICK_SHARE_ACTIONS_TIMEOUT_MS,
                500);
        List<Notification.Action> quickShareActions =
                mScreenshotSmartActions.getSmartActions(
                        mScreenshotId, quickShareActionsFuture, timeoutMs,
                        mSmartActionsProvider, QUICK_SHARE_ACTION);
        if (!quickShareActions.isEmpty()) {
            mQuickShareData.quickShareAction = quickShareActions.get(0);
            mParams.mQuickShareActionsReadyListener.onActionsReady(mQuickShareData);
        }
    }
}
