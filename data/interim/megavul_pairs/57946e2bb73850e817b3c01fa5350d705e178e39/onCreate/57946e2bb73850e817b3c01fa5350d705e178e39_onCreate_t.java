class onCreate {
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPackage = getCallingPackage();
        mVm = getSystemService(VpnManager.class);

        if (mVm.prepareVpn(mPackage, null, UserHandle.myUserId())) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (UserManager.get(this).hasUserRestriction(UserManager.DISALLOW_CONFIG_VPN)) {
            finish();
            return;
        }
        final String alwaysOnVpnPackage = mVm.getAlwaysOnVpnPackageForUser(UserHandle.myUserId());
        // Can't prepare new vpn app when another vpn is always-on
        if (alwaysOnVpnPackage != null && !alwaysOnVpnPackage.equals(mPackage)) {
            finish();
            return;
        }
        mView = View.inflate(this, R.layout.confirm, null);
        ((TextView) mView.findViewById(R.id.warning)).setText(
                Html.fromHtml(getString(R.string.warning, getSanitizedVpnLabel(
                    getVpnLabel().toString(), mPackage)),
                    this /* imageGetter */, null /* tagHandler */));
        mAlertParams.mTitle = getText(R.string.prompt);
        mAlertParams.mPositiveButtonText = getText(android.R.string.ok);
        mAlertParams.mPositiveButtonListener = this;
        mAlertParams.mNegativeButtonText = getText(android.R.string.cancel);
        mAlertParams.mView = mView;
        setupAlert();

        getWindow().setCloseOnTouchOutside(false);
        getWindow().addPrivateFlags(SYSTEM_FLAG_HIDE_NON_SYSTEM_OVERLAY_WINDOWS);
        Button button = mAlert.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setFilterTouchesWhenObscured(true);
    }
}
