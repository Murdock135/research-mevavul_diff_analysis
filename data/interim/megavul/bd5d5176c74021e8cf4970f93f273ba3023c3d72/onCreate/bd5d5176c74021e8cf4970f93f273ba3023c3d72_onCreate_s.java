class onCreate {
@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (icicle != null) {
            if (icicle.containsKey(SAVE_ADDING_USER)) {
                mAddedUserId = icicle.getInt(SAVE_ADDING_USER);
            }
            if (icicle.containsKey(SAVE_REMOVING_USER)) {
                mRemovingUserId = icicle.getInt(SAVE_REMOVING_USER);
            }
            mEditUserInfoController.onRestoreInstanceState(icicle);
        }
        final Context context = getActivity();
        mUserCaps = UserCapabilities.create(context);
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (!mUserCaps.mEnabled) {
            return;
        }

        final int myUserId = UserHandle.myUserId();

        addPreferencesFromResource(R.xml.user_settings);
        mUserListCategory = (PreferenceGroup) findPreference(KEY_USER_LIST);
        mMePreference = new UserPreference(getPrefContext(), null /* attrs */, myUserId,
                null /* settings icon handler */,
                null /* delete icon handler */);
        mMePreference.setKey(KEY_USER_ME);
        mMePreference.setOnPreferenceClickListener(this);
        if (mUserCaps.mIsAdmin) {
            mMePreference.setSummary(R.string.user_admin);
        }
        mAddUser = (DimmableIconPreference) findPreference(KEY_ADD_USER);
        // Determine if add user/profile button should be visible
        if (mUserCaps.mCanAddUser && Utils.isDeviceProvisioned(getActivity())) {
            mAddUser.setOnPreferenceClickListener(this);
            // change label to only mention user, if restricted profiles are not supported
            if (!mUserCaps.mCanAddRestrictedProfile) {
                mAddUser.setTitle(R.string.user_add_user_menu);
            }
        }
        mLockScreenSettings = (PreferenceGroup) findPreference("lock_screen_settings");
        mAddUserWhenLocked = (RestrictedSwitchPreference) findPreference("add_users_when_locked");
        mEmergencyInfoPreference = findPreference(KEY_EMERGENCY_INFO);
        setHasOptionsMenu(true);
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_REMOVED);
        filter.addAction(Intent.ACTION_USER_INFO_CHANGED);
        context.registerReceiverAsUser(mUserChangeReceiver, UserHandle.ALL, filter, null, mHandler);
        loadProfile();
        updateUserList();
        mShouldUpdateUserList = false;
    }
}
