class addAccount {
private void addAccount(String accountType) {
        Bundle addAccountOptions = new Bundle();
        mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
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
