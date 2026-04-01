class onCreate {
@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mContext = getActivity();
        mPm = mContext.getPackageManager();
        mDpm = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mIconDrawableFactory = IconDrawableFactory.newInstance(mContext);
        mServiceListing = new ServiceListing.Builder(mContext)
                .setPermission(CONFIG.permission)
                .setIntentAction(CONFIG.intentAction)
                .setNoun(CONFIG.noun)
                .setSetting(CONFIG.setting)
                .setTag(CONFIG.tag)
                .build();
        mServiceListing.addCallback(this::updateList);

        if (UserManager.get(mContext).isManagedProfile()) {
            // Apps in the work profile do not support notification listeners.
            Toast.makeText(mContext,
                    mDpm.getResources().getString(WORK_APPS_CANNOT_ACCESS_NOTIFICATION_SETTINGS,
                            () -> mContext.getString(R.string.notification_settings_work_profile)),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
