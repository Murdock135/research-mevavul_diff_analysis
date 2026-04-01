class notifyUploadStart {
private void notifyUploadStart(UploadFileOperation upload) {
        // / create status notification with a progress bar
        mLastPercent = 0;
        mNotificationBuilder = NotificationUtils.newNotificationBuilder(this);
        mNotificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.drawable.notification_icon)
            .setTicker(getString(R.string.uploader_upload_in_progress_ticker))
            .setContentTitle(getString(R.string.uploader_upload_in_progress_ticker))
            .setProgress(100, 0, false)
            .setContentText(
                String.format(getString(R.string.uploader_upload_in_progress_content), 0, upload.getFileName())
            );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationBuilder.setChannelId(NotificationUtils.NOTIFICATION_CHANNEL_UPLOAD);
        }

        /// includes a pending intent in the notification showing the details
        Intent intent = UploadListActivity.createIntent(upload.getFile(),
                                                        upload.getUser(),
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP,
                                                        this);
        mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this,
                                                                        (int) System.currentTimeMillis(),
                                                                        intent,
                                                                        0)
                                             );

        if (!upload.isInstantPicture() && !upload.isInstantVideo()) {
            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            }

            mNotificationManager.notify(FOREGROUND_SERVICE_ID, mNotificationBuilder.build());
        }   // else wait until the upload really start (onTransferProgress is called), so that if it's discarded
        // due to lack of Wifi, no notification is shown
        // TODO generalize for automated uploads
    }
}
