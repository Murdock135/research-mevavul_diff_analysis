class createBackgroundPendingIntent {
private PendingIntent createBackgroundPendingIntent(boolean forceService) {
        Context context = AndroidNativeUtil.getContext().getApplicationContext();
        final Class bgListenerClass = getBackgroundLocationListener();
        if (bgListenerClass == null) {
            return null;
        }
        if (!forceService && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, BackgroundLocationBroadcastReceiver.class);
            intent.setData(Uri.parse("http://codenameone.com/a?" + bgListenerClass.getName()));
            intent.setAction(BackgroundLocationBroadcastReceiver.ACTION_PROCESS_UPDATES);
            PendingIntent pendingIntent = AndroidImplementation.getBroadcastPendingIntent(context, 0, intent);
            return pendingIntent;
        } else {


            Intent intent = new Intent(context, BackgroundLocationHandler.class);
            intent.setData(Uri.parse("http://codenameone.com/a?" + bgListenerClass.getName()));
            PendingIntent pendingIntent = AndroidImplementation.getPendingIntent(context, 0,
                    intent);
            return pendingIntent;
        }
    }
}
