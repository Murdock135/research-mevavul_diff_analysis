class handleResponse {
@Override
    protected int handleResponse(final EasResponse response) throws
            IOException, CommandStatusException {
        // resp is either an authentication error, or a good response.
        final int code = response.getStatus();

        if (response.isRedirectError()) {
            final String loc = response.getRedirectAddress();
            if (loc != null && loc.startsWith("http")) {
                LogUtils.d(TAG, "Posting autodiscover to redirect: " + loc);
                mRedirectUri = loc;
                return RESULT_REDIRECT;
            } else {
                LogUtils.w(TAG, "Invalid redirect %s", loc);
                return RESULT_FATAL_SERVER_ERROR;
            }
        }

        if (code == HttpStatus.SC_UNAUTHORIZED) {
            LogUtils.w(TAG, "Autodiscover received SC_UNAUTHORIZED");
            return RESULT_SC_UNAUTHORIZED;
        } else if (code != HttpStatus.SC_OK) {
            // We'll try the next address if this doesn't work
            LogUtils.d(TAG, "Bad response code when posting autodiscover: %d", code);
            return RESULT_BAD_RESPONSE;
        } else {
            mHostAuth = parseAutodiscover(response);
            if (mHostAuth != null) {
                // Fill in the rest of the HostAuth
                // We use the user name and password that were successful during
                // the autodiscover process
                mHostAuth.mLogin = mUsername;
                mHostAuth.mPassword = mPassword;
                // Note: there is no way we can auto-discover the proper client
                // SSL certificate to use, if one is needed.
                mHostAuth.mPort = 443;
                mHostAuth.mProtocol = Eas.PROTOCOL;
                mHostAuth.mFlags = HostAuth.FLAG_SSL | HostAuth.FLAG_AUTHENTICATE;
                return RESULT_OK;
            } else {
                return RESULT_HARD_DATA_FAILURE;
            }
        }
    }
}
