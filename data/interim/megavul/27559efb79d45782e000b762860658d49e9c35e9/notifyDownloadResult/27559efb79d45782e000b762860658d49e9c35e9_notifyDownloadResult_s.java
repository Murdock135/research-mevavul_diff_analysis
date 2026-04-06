class notifyDownloadResult {
@SuppressFBWarnings("DMI")
    private void notifyDownloadResult(DownloadFileOperation download,
                                      RemoteOperationResult downloadResult) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        if (!downloadResult.isCancelled()) {
            if (downloadResult.isSuccess()) {
                if (conflictUploadId > 0) {
                    uploadsStorageManager.removeUpload(conflictUploadId);
                }
                // Dont show notification except an error has occured.
                return;
            }
            int tickerId = downloadResult.isSuccess() ?
                    R.string.downloader_download_succeeded_ticker : R.string.downloader_download_failed_ticker;

            boolean needsToUpdateCredentials = ResultCode.UNAUTHORIZED.equals(downloadResult.getCode());
            tickerId = needsToUpdateCredentials ?
                    R.string.downloader_download_failed_credentials_error : tickerId;

            mNotificationBuilder
                    .setTicker(getString(tickerId))
                    .setContentTitle(getString(tickerId))
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setProgress(0, 0, false);

            if (needsToUpdateCredentials) {
                configureUpdateCredentialsNotification(download.getAccount());

            } else {
                // TODO put something smart in showDetailsIntent
                Intent showDetailsIntent = new Intent();
                mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                        showDetailsIntent, 0));
            }

            mNotificationBuilder.setContentText(ErrorMessageAdapter.getErrorCauseMessage(downloadResult,
                    download, getResources()));

            if (mNotificationManager != null) {
                mNotificationManager.notify((new SecureRandom()).nextInt(), mNotificationBuilder.build());

                // Remove success notification
                if (downloadResult.isSuccess()) {
                    // Sleep 2 seconds, so show the notification before remove it
                    NotificationUtils.cancelWithDelay(mNotificationManager,
                                                      R.string.downloader_download_succeeded_ticker, 2000);
                }
            }
        }
    }
}
