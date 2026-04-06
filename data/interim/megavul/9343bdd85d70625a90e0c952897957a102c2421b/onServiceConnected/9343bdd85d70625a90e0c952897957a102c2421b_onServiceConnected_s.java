class onServiceConnected {
@Override
        public void onServiceConnected(ComponentName component, IBinder service) {
            if (component.equals(
                new ComponentName(AuthenticatorActivity.this, OperationsService.class)
            )) {
                mOperationsServiceBinder = (OperationsServiceBinder) service;

                Uri data = getIntent().getData();
                if (data != null && data.toString().startsWith(getString(R.string.login_data_own_scheme))) {
                    String prefix = getString(R.string.login_data_own_scheme) + PROTOCOL_SUFFIX + "login/";
                    LoginUrlInfo loginUrlInfo = parseLoginDataUrl(prefix, data.toString());

                    try {
                        mServerInfo.mBaseUrl = AuthenticatorUrlUtils.normalizeUrlSuffix(loginUrlInfo.serverAddress);
                        webViewUser = loginUrlInfo.username;
                        webViewPassword = loginUrlInfo.password;
                        doOnResumeAndBound();
                        checkOcServer();
                    } catch (Exception e) {
                        mServerStatusIcon = R.drawable.ic_alert;
                        mServerStatusText = getString(R.string.qr_could_not_be_read);
                        showServerStatus();
                    }
                } else {
                    doOnResumeAndBound();
                }
            }
        }
}
