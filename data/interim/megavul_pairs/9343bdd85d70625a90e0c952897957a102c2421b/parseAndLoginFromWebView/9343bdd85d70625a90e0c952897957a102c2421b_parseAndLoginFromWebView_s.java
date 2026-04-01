class parseAndLoginFromWebView {
private void parseAndLoginFromWebView(String dataString) {
        String prefix = getString(R.string.login_data_own_scheme) + PROTOCOL_SUFFIX + "login/";
        LoginUrlInfo loginUrlInfo = parseLoginDataUrl(prefix, dataString);

        try {
            if (mHostUrlInput != null) {
                mHostUrlInput.setText("");
            }
            mServerInfo.mBaseUrl = AuthenticatorUrlUtils.normalizeUrlSuffix(loginUrlInfo.serverAddress);
            webViewUser = loginUrlInfo.username;
            webViewPassword = loginUrlInfo.password;
        } catch (Exception e) {
            mServerStatusIcon = R.drawable.ic_alert;
            mServerStatusText = getString(R.string.qr_could_not_be_read);
            showServerStatus();
        }
        checkOcServer();
    }
}
