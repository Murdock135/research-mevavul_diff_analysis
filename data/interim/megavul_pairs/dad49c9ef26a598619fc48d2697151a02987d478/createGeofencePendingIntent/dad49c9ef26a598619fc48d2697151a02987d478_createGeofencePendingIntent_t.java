class createGeofencePendingIntent {
private PendingIntent createGeofencePendingIntent(Class geofenceListenerClass, com.codename1.location.Geofence gf, boolean forceService) {
        Context context = AndroidNativeUtil.getContext().getApplicationContext();


        if (!forceService && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (geofencePendingIntent != null) {
                return geofencePendingIntent;
            }
            Intent intent = new Intent(context, BackgroundLocationBroadcastReceiver.class);
            intent.setAction(BackgroundLocationBroadcastReceiver.ACTION_PROCESS_GEOFENCE_TRANSITIONS);
            intent.setData(Uri.parse("http://codenameone.com/a?" + geofenceListenerClass.getName()));
            //intent.setAction(BackgroundLocationBroadcastReceiver.ACTION_PROCESS_GEOFENCE_TRANSITIONS);
            geofencePendingIntent = AndroidImplementation.getBroadcastPendingIntent(AndroidNativeUtil.getContext().getApplicationContext(), 0, intent);
            return geofencePendingIntent;
        } else {

            Intent intent = new Intent(context, GeofenceHandler.class);
            intent.putExtra("geofenceClass", geofenceListenerClass.getName());
            intent.putExtra("geofenceID", gf.getId());
            PendingIntent pendingIntent = AndroidImplementation.getPendingIntent(context, 0,
                    intent);


            return pendingIntent;
        }
    }
}
