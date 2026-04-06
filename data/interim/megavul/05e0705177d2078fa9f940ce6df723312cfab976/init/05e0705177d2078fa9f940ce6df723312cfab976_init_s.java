class init {
public void init() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_SHOW_BATTERY_SETTINGS);
            filter.addAction(ACTION_START_SAVER);
            filter.addAction(ACTION_STOP_SAVER);
            filter.addAction(ACTION_DISMISSED_WARNING);
            mContext.registerReceiverAsUser(this, UserHandle.ALL, filter, null, mHandler);
        }
}
