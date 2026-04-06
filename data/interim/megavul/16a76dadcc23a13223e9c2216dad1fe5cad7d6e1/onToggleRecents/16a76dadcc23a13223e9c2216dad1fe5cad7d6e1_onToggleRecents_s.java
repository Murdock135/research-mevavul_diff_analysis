class onToggleRecents {
@ProxyFromPrimaryToCurrentUser
    public void onToggleRecents() {
        if (mSystemServicesProxy.isForegroundUserOwner()) {
            toggleRecents();
        } else {
            Intent intent = createLocalBroadcastIntent(mContext,
                    RecentsUserEventProxyReceiver.ACTION_PROXY_TOGGLE_RECENTS_TO_USER);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
    }
}
