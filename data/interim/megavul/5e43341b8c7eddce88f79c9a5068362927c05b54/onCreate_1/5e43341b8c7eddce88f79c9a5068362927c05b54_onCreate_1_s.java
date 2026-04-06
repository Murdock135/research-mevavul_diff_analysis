class onCreate_1 {
@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String chooseLockAction = getActivity().getIntent().getAction();
            mFingerprintManager = Utils.getFingerprintManagerOrNull(getActivity());
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mKeyStore = KeyStore.getInstance();
            mChooseLockSettingsHelper = new ChooseLockSettingsHelper(this.getActivity());
            mLockPatternUtils = new LockPatternUtils(getActivity());
            mIsSetNewPassword = ACTION_SET_NEW_PARENT_PROFILE_PASSWORD.equals(chooseLockAction)
                    || ACTION_SET_NEW_PASSWORD.equals(chooseLockAction);

            // Defaults to needing to confirm credentials
            final boolean confirmCredentials = getActivity().getIntent()
                .getBooleanExtra(CONFIRM_CREDENTIALS, true);
            if (getActivity() instanceof ChooseLockGeneric.InternalActivity) {
                mPasswordConfirmed = !confirmCredentials;
                mUserPassword = getActivity().getIntent().getStringExtra(
                        ChooseLockSettingsHelper.EXTRA_KEY_PASSWORD);
            }
            mHideDrawer = getActivity().getIntent().getBooleanExtra(EXTRA_HIDE_DRAWER, false);

            mHasChallenge = getActivity().getIntent().getBooleanExtra(
                    ChooseLockSettingsHelper.EXTRA_KEY_HAS_CHALLENGE, false);
            mChallenge = getActivity().getIntent().getLongExtra(
                    ChooseLockSettingsHelper.EXTRA_KEY_CHALLENGE, 0);
            mForFingerprint = getActivity().getIntent().getBooleanExtra(
                    ChooseLockSettingsHelper.EXTRA_KEY_FOR_FINGERPRINT, false);
            mForChangeCredRequiredForBoot = getArguments() != null && getArguments().getBoolean(
                    ChooseLockSettingsHelper.EXTRA_KEY_FOR_CHANGE_CRED_REQUIRED_FOR_BOOT);
            mUserManager = UserManager.get(getActivity());

            if (savedInstanceState != null) {
                mPasswordConfirmed = savedInstanceState.getBoolean(PASSWORD_CONFIRMED);
                mWaitingForConfirmation = savedInstanceState.getBoolean(WAITING_FOR_CONFIRMATION);
                mEncryptionRequestQuality = savedInstanceState.getInt(ENCRYPT_REQUESTED_QUALITY);
                mEncryptionRequestDisabled = savedInstanceState.getBoolean(
                        ENCRYPT_REQUESTED_DISABLED);
                if (mUserPassword == null) {
                    mUserPassword = savedInstanceState.getString(
                            ChooseLockSettingsHelper.EXTRA_KEY_PASSWORD);
                }
            }

            // a) If this is started from other user, use that user id.
            // b) If this is started from the same user, read the extra if this is launched
            //    from Settings app itself.
            // c) Otherwise, use UserHandle.myUserId().
            mUserId = Utils.getSecureTargetUser(
                    getActivity().getActivityToken(),
                    UserManager.get(getActivity()),
                    getArguments(),
                    getActivity().getIntent().getExtras()).getIdentifier();
            mController = new ChooseLockGenericController(getContext(), mUserId);
            if (ACTION_SET_NEW_PASSWORD.equals(chooseLockAction)
                    && UserManager.get(getActivity()).isManagedProfile(mUserId)
                    && mLockPatternUtils.isSeparateProfileChallengeEnabled(mUserId)) {
                getActivity().setTitle(R.string.lock_settings_picker_title_profile);
            }

            mManagedPasswordProvider = ManagedLockPasswordProvider.get(getActivity(), mUserId);

            if (mPasswordConfirmed) {
                updatePreferencesOrFinish(savedInstanceState != null);
                if (mForChangeCredRequiredForBoot) {
                    maybeEnableEncryption(mLockPatternUtils.getKeyguardStoredPasswordQuality(
                            mUserId), false);
                }
            } else if (!mWaitingForConfirmation) {
                ChooseLockSettingsHelper helper =
                        new ChooseLockSettingsHelper(this.getActivity(), this);
                boolean managedProfileWithUnifiedLock =
                        UserManager.get(getActivity()).isManagedProfile(mUserId)
                        && !mLockPatternUtils.isSeparateProfileChallengeEnabled(mUserId);
                boolean skipConfirmation = managedProfileWithUnifiedLock && !mIsSetNewPassword;
                if (skipConfirmation
                        || !helper.launchConfirmationActivity(CONFIRM_EXISTING_REQUEST,
                        getString(R.string.unlock_set_unlock_launch_picker_title), true, mUserId)) {
                    mPasswordConfirmed = true; // no password set, so no need to confirm
                    updatePreferencesOrFinish(savedInstanceState != null);
                } else {
                    mWaitingForConfirmation = true;
                }
            }
            addHeaderView();
        }
}
