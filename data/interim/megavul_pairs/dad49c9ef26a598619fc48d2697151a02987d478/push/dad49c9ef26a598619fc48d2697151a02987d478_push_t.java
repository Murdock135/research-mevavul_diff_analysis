class push {
@Override
    public void push(final String value) {
        final PushCallback callback = getPushCallbackInstance();
        if(callback != null) {
            Display.getInstance().callSerially(new Runnable() {
                public void run() {
                    callback.push(value);
                }
            });
        } else {
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent newIntent = new Intent(this, getStubClass());
            PendingIntent contentIntent = AndroidImplementation.createPendingIntent(this, 0, newIntent);



            Notification.Builder builder = new Notification.Builder(this)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(android.R.drawable.stat_notify_sync)
                    .setTicker(value)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(value)

                    .setDefaults(Notification.DEFAULT_ALL);




            // The following section is commented out so that builds against SDKs below 26
            // won't fail.
            /*<SDK26>
            if(android.os.Build.VERSION.SDK_INT >= 21){
                builder.setCategory("Notification");
            }
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                String id = getProperty("android.NotificationChannel.id", "cn1-channel");

                CharSequence name = getProperty("android.NotificationChannel.name", "Notifications");

                String description = getProperty("android.NotificationChannel.description", "Remote notifications");

                int importance = Integer.parseInt(getProperty("android.NotificationChannel.importance", ""+NotificationManager.IMPORTANCE_HIGH));

                android.app.NotificationChannel mChannel = new android.app.NotificationChannel(id, name,importance);

                mChannel.setDescription(description);

                mChannel.enableLights(Boolean.parseBoolean(getProperty("android.NotificationChannel.enableLights", "true")));

                mChannel.setLightColor(Integer.parseInt(getProperty("android.NotificationChannel.lightColor", ""+android.graphics.Color.RED)));

                mChannel.enableVibration(Boolean.parseBoolean(getProperty("android.NotificationChannel.enableVibration", "false")));
                String vibrationPatternStr = getProperty("android.NotificationChannel.vibrationPattern", null);
                if (vibrationPatternStr != null) {
                    String[] parts = vibrationPatternStr.split(",");
                    int len = parts.length;
                    long[] pattern = new long[len];
                    for (int i=0; i<len; i++) {
                        pattern[i] = Long.parseLong(parts[i].trim());
                    }
                    mChannel.setVibrationPattern(pattern);
                }



                mNotificationManager.createNotificationChannel(mChannel);
                System.out.println("Setting push channel to "+id);
                builder.setChannelId(id);
            }
            </SDK26>*/

            Notification notif = builder.build();
            int notifId = getNotifyId();//(int)System.currentTimeMillis();

            //notif.extras.putInt("notificationId", notifId);
            nm.notify(notifId, notif);
        }
    }
}
