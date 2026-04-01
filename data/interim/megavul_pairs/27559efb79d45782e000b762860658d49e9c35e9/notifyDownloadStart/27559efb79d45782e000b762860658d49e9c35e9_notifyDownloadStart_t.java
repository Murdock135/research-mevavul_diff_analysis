class notifyDownloadStart {
private void notifyDownloadStart(DownloadFileOperation download) {
        /// create status notification with a progress bar
        mLastPercent = 0;
        mNotificationBuilder = NotificationUtils.newNotificationBuilder(this);
        mNotificationBuilder
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(getString(R.string.downloader_download_in_progress_ticker))
                .setContentTitle(getString(R.string.downloader_download_in_progress_ticker))
                .setOngoing(true)
                .setProgress(100, 0, download.getSize() < 0)
                .setContentText(
                        String.format(getString(R.string.downloader_download_in_progress_content), 0,
                                new File(download.getSavePath()).getName())
                );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder.setChannelId(NotificationUtils.NOTIFICATION_CHANNEL_DOWNLOAD);
        }

        /// includes a pending intent in the notification showing the details view of the file
        Intent showDetailsIntent = null;
        if (PreviewImageFragment.canBePreviewed(download.getFile())) {
            showDetailsIntent = new Intent(this, PreviewImageActivity.class);
        } else {
            showDetailsIntent = new Intent(this, FileDisplayActivity.class);
        }
        showDetailsIntent.putExtra(FileActivity.EXTRA_FILE, download.getFile());
        showDetailsIntent.putExtra(FileActivity.EXTRA_USER, download.getAccount());
        showDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                                                                        showDetailsIntent, PendingIntent.FLAG_IMMUTABLE));


        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        if (mNotificationManager != null) {
            mNotificationManager.notify(R.string.downloader_download_in_progress_ticker, mNotificationBuilder.build());
        }
    }
}
