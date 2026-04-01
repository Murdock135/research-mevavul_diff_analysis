class onPreloadRecents {
@ProxyFromPrimaryToCurrentUser
    public void onPreloadRecents() {
        // Ensure the device has been provisioned before allowing the user to interact with
        // recents
        if (!isDeviceProvisioned()) {
            return;
        }

        if (mSystemServicesProxy.isForegroundUserOwner()) {
            preloadRecents();
        } else {
            Intent intent = createLocalBroadcastIntent(mContext,
                    RecentsUserEventProxyReceiver.ACTION_PROXY_PRELOAD_RECENTS_TO_USER);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
    }
}
