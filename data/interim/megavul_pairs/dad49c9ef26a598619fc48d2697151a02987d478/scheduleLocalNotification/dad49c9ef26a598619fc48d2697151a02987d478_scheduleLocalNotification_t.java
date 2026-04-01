class scheduleLocalNotification {
public void scheduleLocalNotification(LocalNotification notif, long firstTime, int repeat) {

        final Intent notificationIntent = new Intent(getContext(), LocalNotificationPublisher.class);
        notificationIntent.setAction(getContext().getApplicationInfo().packageName + "." + notif.getId());
        notificationIntent.putExtra(LocalNotificationPublisher.NOTIFICATION, createBundleFromNotification(notif));

        Intent contentIntent = new Intent();
        if (activityComponentName != null) {
            contentIntent.setComponent(activityComponentName);
        } else {
            try {
                contentIntent.setComponent(getContext().getPackageManager().getLaunchIntentForPackage(getContext().getApplicationInfo().packageName).getComponent());
            } catch (Exception ex) {
                System.err.println("Failed to get the component name for local notification.  Local notification may not work.");
                ex.printStackTrace();
            }
        }
        contentIntent.putExtra("LocalNotificationID", notif.getId());

        if (BACKGROUND_FETCH_NOTIFICATION_ID.equals(notif.getId()) && getBackgroundFetchListener() != null) {
            Context context = AndroidNativeUtil.getContext();

            Intent intent = new Intent(context, BackgroundFetchHandler.class);
            //there is an bug that causes this to not to workhttps://code.google.com/p/android/issues/detail?id=81812
            //intent.putExtra("backgroundClass", getBackgroundLocationListener().getName());
            //an ugly workaround to the putExtra bug 
            intent.setData(Uri.parse("http://codenameone.com/a?" + getBackgroundFetchListener().getClass().getName()));
            PendingIntent pendingIntent = getPendingIntent(context, 0,
                    intent);
            notificationIntent.putExtra(LocalNotificationPublisher.BACKGROUND_FETCH_INTENT, pendingIntent);

        } else {
            contentIntent.setData(Uri.parse("http://codenameone.com/a?LocalNotificationID="+Uri.encode(notif.getId())));
        }
        PendingIntent pendingContentIntent = createPendingIntent(getContext(), 0, contentIntent);

        notificationIntent.putExtra(LocalNotificationPublisher.NOTIFICATION_INTENT, pendingContentIntent);


        PendingIntent pendingIntent = getBroadcastPendingIntent(getContext(), 0, notificationIntent);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (BACKGROUND_FETCH_NOTIFICATION_ID.equals(notif.getId())) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, getPreferredBackgroundFetchInterval() * 1000, pendingIntent);
        } else {
            if(repeat == LocalNotification.REPEAT_NONE){
                alarmManager.set(AlarmManager.RTC_WAKEUP, firstTime, pendingIntent);

            }else if(repeat == LocalNotification.REPEAT_MINUTE){

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, 60*1000, pendingIntent);

            }else if(repeat == LocalNotification.REPEAT_HOUR){

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

            }else if(repeat == LocalNotification.REPEAT_DAY){

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_DAY, pendingIntent);

            }else if(repeat == LocalNotification.REPEAT_WEEK){

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_DAY * 7, pendingIntent);

            }
        }
    }
}
