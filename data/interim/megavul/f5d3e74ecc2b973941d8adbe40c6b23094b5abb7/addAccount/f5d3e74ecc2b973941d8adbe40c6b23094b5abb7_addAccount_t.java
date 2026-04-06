class addAccount {
private void addAccount(String accountType) {
        Bundle addAccountOptions = new Bundle();
        /*
         * The identityIntent is for the purposes of establishing the identity
         * of the caller and isn't intended for launching activities, services
         * or broadcasts.
         *
         * Unfortunately for legacy reasons we still need to support this. But
         * we can cripple the intent so that 3rd party authenticators can't
         * fill in addressing information and launch arbitrary actions.
         */
        Intent identityIntent = new Intent();
        identityIntent.setComponent(new ComponentName(SHOULD_NOT_RESOLVE, SHOULD_NOT_RESOLVE));
        identityIntent.setAction(SHOULD_NOT_RESOLVE);
        identityIntent.addCategory(SHOULD_NOT_RESOLVE);

        mPendingIntent = PendingIntent.getBroadcast(this, 0, identityIntent, 0);
        addAccountOptions.putParcelable(KEY_CALLER_IDENTITY, mPendingIntent);
        addAccountOptions.putBoolean(EXTRA_HAS_MULTIPLE_USERS, Utils.hasMultipleUsers(this));
        AccountManager.get(this).addAccountAsUser(
                accountType,
                null, /* authTokenType */
                null, /* requiredFeatures */
                addAccountOptions,
                null,
                mCallback,
                null /* handler */,
                mUserHandle);
        mAddAccountCalled  = true;
    }
}
