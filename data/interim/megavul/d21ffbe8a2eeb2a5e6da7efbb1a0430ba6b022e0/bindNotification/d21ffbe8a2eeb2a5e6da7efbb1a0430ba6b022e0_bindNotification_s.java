class bindNotification {
public void bindNotification(
            @Action int selectedAction,
            ShortcutManager shortcutManager,
            PackageManager pm,
            PeopleSpaceWidgetManager peopleSpaceWidgetManager,
            INotificationManager iNotificationManager,
            OnUserInteractionCallback onUserInteractionCallback,
            String pkg,
            NotificationChannel notificationChannel,
            NotificationEntry entry,
            Notification.BubbleMetadata bubbleMetadata,
            OnSettingsClickListener onSettingsClick,
            ConversationIconFactory conversationIconFactory,
            Context userContext,
            boolean isDeviceProvisioned,
            @Main Handler mainHandler,
            @Background Handler bgHandler,
            OnConversationSettingsClickListener onConversationSettingsClickListener,
            Optional<BubblesManager> bubblesManagerOptional,
            ShadeController shadeController) {
        mPressedApply = false;
        mSelectedAction = selectedAction;
        mINotificationManager = iNotificationManager;
        mPeopleSpaceWidgetManager = peopleSpaceWidgetManager;
        mOnUserInteractionCallback = onUserInteractionCallback;
        mPackageName = pkg;
        mEntry = entry;
        mSbn = entry.getSbn();
        mPm = pm;
        mAppName = mPackageName;
        mOnSettingsClickListener = onSettingsClick;
        mNotificationChannel = notificationChannel;
        mAppUid = mSbn.getUid();
        mDelegatePkg = mSbn.getOpPkg();
        mIsDeviceProvisioned = isDeviceProvisioned;
        mOnConversationSettingsClickListener = onConversationSettingsClickListener;
        mIconFactory = conversationIconFactory;
        mUserContext = userContext;
        mBubbleMetadata = bubbleMetadata;
        mBubblesManagerOptional = bubblesManagerOptional;
        mShadeController = shadeController;
        mMainHandler = mainHandler;
        mBgHandler = bgHandler;
        mShortcutManager = shortcutManager;
        mShortcutInfo = entry.getRanking().getConversationShortcutInfo();
        if (mShortcutInfo == null) {
            throw new IllegalArgumentException("Does not have required information");
        }

        mNotificationChannel = NotificationChannelHelper.createConversationChannelIfNeeded(
                getContext(), mINotificationManager, entry, mNotificationChannel);

        try {
            mAppBubble = mINotificationManager.getBubblePreferenceForPackage(mPackageName, mAppUid);
        } catch (RemoteException e) {
            Log.e(TAG, "can't reach OS", e);
            mAppBubble = BUBBLE_PREFERENCE_SELECTED;
        }

        bindHeader();
        bindActions();

        View done = findViewById(R.id.done);
        done.setOnClickListener(mOnDone);
        done.setAccessibilityDelegate(mGutsContainer.getAccessibilityDelegate());
    }
}
