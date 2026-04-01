class stopReceivingPush {
public void stopReceivingPush() {
        Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        unregIntent.setPackage("com.google.android.gms");
        unregIntent.putExtra("app", AndroidImplementation.getBroadcastPendingIntent(this, 0, new Intent()));
        startService(unregIntent);
    }
}
