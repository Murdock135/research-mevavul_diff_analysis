class initializeConversationNotificationInfo {
@VisibleForTesting
    void initializeConversationNotificationInfo(
            final ExpandableNotificationRow row,
            NotificationConversationInfo notificationInfoView) throws Exception {
        NotificationGuts guts = row.getGuts();
        NotificationEntry entry = row.getEntry();
        StatusBarNotification sbn = entry.getSbn();
        String packageName = sbn.getPackageName();
        // Settings link is only valid for notifications that specify a non-system user
        NotificationConversationInfo.OnSettingsClickListener onSettingsClick = null;
        UserHandle userHandle = sbn.getUser();
        PackageManager pmUser = CentralSurfaces.getPackageManagerForUser(
                mContext, userHandle.getIdentifier());
        final NotificationConversationInfo.OnAppSettingsClickListener onAppSettingsClick =
                (View v, Intent intent) -> {
                    mMetricsLogger.action(MetricsProto.MetricsEvent.ACTION_APP_NOTE_SETTINGS);
                    guts.resetFalsingCheck();
                    mNotificationActivityStarter.startNotificationGutsIntent(intent, sbn.getUid(),
                            row);
                };

        final NotificationConversationInfo.OnConversationSettingsClickListener
                onConversationSettingsListener =
                () -> {
                    startConversationSettingsActivity(sbn.getUid(), row);
                };

        if (!userHandle.equals(UserHandle.ALL)
                || mLockscreenUserManager.getCurrentUserId() == UserHandle.USER_SYSTEM) {
            onSettingsClick = (View v, NotificationChannel channel, int appUid) -> {
                mMetricsLogger.action(MetricsProto.MetricsEvent.ACTION_NOTE_INFO);
                guts.resetFalsingCheck();
                mOnSettingsClickListener.onSettingsClick(sbn.getKey());
                startAppNotificationSettingsActivity(packageName, appUid, channel, row);
            };
        }
        ConversationIconFactory iconFactoryLoader = new ConversationIconFactory(mContext,
                mLauncherApps, pmUser, IconDrawableFactory.newInstance(mContext, false),
                mContext.getResources().getDimensionPixelSize(
                        R.dimen.notification_guts_conversation_icon_size));

        notificationInfoView.bindNotification(
                notificationInfoView.getSelectedAction(),
                mShortcutManager,
                pmUser,
                mUserManager,
                mPeopleSpaceWidgetManager,
                mNotificationManager,
                mOnUserInteractionCallback,
                packageName,
                entry.getChannel(),
                entry,
                entry.getBubbleMetadata(),
                onSettingsClick,
                iconFactoryLoader,
                mContextTracker.getUserContext(),
                mDeviceProvisionedController.isDeviceProvisioned(),
                mMainHandler,
                mBgHandler,
                onConversationSettingsListener,
                mBubblesManagerOptional,
                mShadeController);
    }
}
