class cancelLocalNotification {
public void cancelLocalNotification(String notificationId) {
        Intent notificationIntent = new Intent(getContext(), LocalNotificationPublisher.class);
        notificationIntent.setAction(getContext().getApplicationInfo().packageName + "." + notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
