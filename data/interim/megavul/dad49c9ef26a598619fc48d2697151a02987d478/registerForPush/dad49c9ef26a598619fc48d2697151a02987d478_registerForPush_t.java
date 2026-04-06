class registerForPush {
public void registerForPush(String key) {
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.setPackage("com.google.android.gms");
        registrationIntent.putExtra("app", AndroidImplementation.getBroadcastPendingIntent(this, 0, new Intent())); // boilerplate
        registrationIntent.putExtra("sender", key);
        startService(registrationIntent);
    }
}
