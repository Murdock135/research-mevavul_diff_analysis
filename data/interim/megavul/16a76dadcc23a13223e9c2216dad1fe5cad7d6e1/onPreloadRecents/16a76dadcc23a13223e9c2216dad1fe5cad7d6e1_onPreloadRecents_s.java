class onPreloadRecents {
@ProxyFromPrimaryToCurrentUser
    public void onPreloadRecents() {
        if (mSystemServicesProxy.isForegroundUserOwner()) {
            preloadRecents();
        } else {
            Intent intent = createLocalBroadcastIntent(mContext,
                    RecentsUserEventProxyReceiver.ACTION_PROXY_PRELOAD_RECENTS_TO_USER);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
    }
}
