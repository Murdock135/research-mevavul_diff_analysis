class clearListener {
@Override
    protected void clearListener() {
        final Class bgListenerClass = getBackgroundLocationListener();
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                //mGoogleApiClient must be connected
                while (!getmGoogleApiClient().isConnected()) {
                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {
                    }
                }
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {

                    public void run() {
                        if (inMemoryBackgroundLocationListener != null) {
                            Context context = AndroidNativeUtil.getContext();
                            Intent intent = new Intent(context, BackgroundLocationHandler.class);
                            if (bgListenerClass != null) {
                                intent.putExtra("backgroundClass", bgListenerClass.getName());
                            }
                            PendingIntent pendingIntent = AndroidImplementation.getPendingIntent(context, 0,
                                    intent);

                            //LocationServices.FusedLocationApi.removeLocationUpdates(getmGoogleApiClient(), pendingIntent);
                            removeLocationUpdates(context, pendingIntent);
                            inMemoryBackgroundLocationListener = null;
                        } else {
                            //LocationServices.FusedLocationApi.removeLocationUpdates(getmGoogleApiClient(), AndroidLocationPlayServiceManager.this);
                            removeLocationUpdates(AndroidNativeUtil.getContext(), AndroidLocationPlayServiceManager.this);
                        }
                    }
                });
            }
        });
        t.setUncaughtExceptionHandler(AndroidImplementation.exceptionHandler);
        t.start();
    }
}
