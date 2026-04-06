class createPendingIntent {
private PendingIntent createPendingIntent(@NonNull Intent intent, @NonNull Notification notification, @NonNull User user) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, notification.getNotificationId(),
                                         putExtrasToIntent(intent, notification, user),
                                         PendingIntent.FLAG_ONE_SHOT);
    }
}
