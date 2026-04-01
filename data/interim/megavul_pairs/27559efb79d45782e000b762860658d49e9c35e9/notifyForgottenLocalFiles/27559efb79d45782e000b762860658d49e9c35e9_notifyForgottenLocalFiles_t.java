class notifyForgottenLocalFiles {
private void notifyForgottenLocalFiles() {
        NotificationCompat.Builder notificationBuilder = createNotificationBuilder();
        notificationBuilder.setTicker(i18n(R.string.sync_foreign_files_forgotten_ticker));

        /// includes a pending intent in the notification showing a more detailed explanation
        Intent explanationIntent = new Intent(getContext(), ErrorsWhileCopyingHandlerActivity.class);
        explanationIntent.putExtra(ErrorsWhileCopyingHandlerActivity.EXTRA_USER, getUser());
        ArrayList<String> remotePaths = new ArrayList<String>();
        ArrayList<String> localPaths = new ArrayList<String>();
        remotePaths.addAll(mForgottenLocalFiles.keySet());
        localPaths.addAll(mForgottenLocalFiles.values());
        explanationIntent.putExtra(ErrorsWhileCopyingHandlerActivity.EXTRA_LOCAL_PATHS, localPaths);
        explanationIntent.putExtra(ErrorsWhileCopyingHandlerActivity.EXTRA_REMOTE_PATHS, remotePaths);
        explanationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notificationBuilder
            .setContentIntent(PendingIntent.getActivity(
                getContext(), (int) System.currentTimeMillis(), explanationIntent, PendingIntent.FLAG_IMMUTABLE
                                                       ))
            .setContentTitle(i18n(R.string.sync_foreign_files_forgotten_ticker))
            .setContentText(getQuantityString(
                    R.plurals.sync_foreign_files_forgotten_content,
                    mForgottenLocalFiles.size(),
                    mForgottenLocalFiles.size(),
                    i18n(R.string.app_name))
            );

        showNotification(R.string.sync_foreign_files_forgotten_ticker, notificationBuilder);
    }
}
