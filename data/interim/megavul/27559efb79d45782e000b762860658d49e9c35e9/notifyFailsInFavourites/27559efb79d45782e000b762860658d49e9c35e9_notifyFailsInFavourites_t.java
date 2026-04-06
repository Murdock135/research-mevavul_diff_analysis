class notifyFailsInFavourites {
private void notifyFailsInFavourites() {
        if (mFailedResultsCounter > 0) {
            NotificationCompat.Builder notificationBuilder = createNotificationBuilder();
            notificationBuilder.setTicker(i18n(R.string.sync_fail_in_favourites_ticker));

            // TODO put something smart in the contentIntent below
            notificationBuilder
                .setContentIntent(PendingIntent.getActivity(
                    getContext(), (int) System.currentTimeMillis(), new Intent(), PendingIntent.FLAG_IMMUTABLE
                                                           ))
                .setContentTitle(i18n(R.string.sync_fail_in_favourites_ticker))
                .setContentText(getQuantityString(
                    R.plurals.sync_fail_in_favourites_content,
                    mFailedResultsCounter,
                    mFailedResultsCounter + mConflictsFound, mConflictsFound
                    )
                );

            showNotification(R.string.sync_fail_in_favourites_ticker, notificationBuilder);
        } else {
            NotificationCompat.Builder notificationBuilder = createNotificationBuilder();
            notificationBuilder.setTicker(i18n(R.string.sync_conflicts_in_favourites_ticker));

            // TODO put something smart in the contentIntent below
            notificationBuilder
                .setContentIntent(PendingIntent.getActivity(
                    getContext(), (int) System.currentTimeMillis(), new Intent(), PendingIntent.FLAG_IMMUTABLE
                                                           ))
                .setContentTitle(i18n(R.string.sync_conflicts_in_favourites_ticker))
                .setContentText(i18n(R.string.sync_conflicts_in_favourites_ticker, mConflictsFound));

            showNotification(R.string.sync_conflicts_in_favourites_ticker, notificationBuilder);
        }
    }
}
