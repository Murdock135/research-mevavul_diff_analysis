class configureUpdateCredentialsNotification {
private void configureUpdateCredentialsNotification(Account account) {
        // let the user update credentials with one click
        Intent updateAccountCredentials = new Intent(this, AuthenticatorActivity.class);
        updateAccountCredentials.putExtra(AuthenticatorActivity.EXTRA_ACCOUNT, account);
        updateAccountCredentials.putExtra(
                AuthenticatorActivity.EXTRA_ACTION,
                AuthenticatorActivity.ACTION_UPDATE_EXPIRED_TOKEN
        );
        updateAccountCredentials.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        updateAccountCredentials.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        updateAccountCredentials.addFlags(Intent.FLAG_FROM_BACKGROUND);
        mNotificationBuilder.setContentIntent(
            PendingIntent.getActivity(this,
                                      (int) System.currentTimeMillis(),
                                      updateAccountCredentials,
                                      PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE)
                                             );
    }
}
