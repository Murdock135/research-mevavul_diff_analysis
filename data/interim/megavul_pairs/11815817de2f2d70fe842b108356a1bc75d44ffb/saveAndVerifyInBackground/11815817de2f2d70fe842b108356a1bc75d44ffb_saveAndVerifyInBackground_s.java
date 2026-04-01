class saveAndVerifyInBackground {
@Override
        protected Pair<Boolean, Intent> saveAndVerifyInBackground() {
            final boolean success = mUtils.setLockCredential(
                    mChosenPassword, mCurrentCredential, mUserId);
            if (success) {
                unifyProfileCredentialIfRequested();
            }
            Intent result = null;
            if (success && mRequestGatekeeperPassword) {
                // If a Gatekeeper Password was requested, invoke the LockSettingsService code
                // path to return a Gatekeeper Password based on the credential that the user
                // chose. This should only be run if the credential was successfully set.
                final VerifyCredentialResponse response = mUtils.verifyCredential(mChosenPassword,
                        mUserId, LockPatternUtils.VERIFY_FLAG_REQUEST_GK_PW_HANDLE);

                if (!response.isMatched() || !response.containsGatekeeperPasswordHandle()) {
                    Log.e(TAG, "critical: bad response or missing GK PW handle for known good"
                            + " password: " + response.toString());
                }

                result = new Intent();
                result.putExtra(ChooseLockSettingsHelper.EXTRA_KEY_GK_PW_HANDLE,
                        response.getGatekeeperPasswordHandle());
            }
            return Pair.create(success, result);
        }
}
