class onToggleRecents {
@ProxyFromPrimaryToCurrentUser
    public void onToggleRecents() {
        // Ensure the device has been provisioned before allowing the user to interact with
        // recents
        if (!isDeviceProvisioned()) {
            return;
        }

        if (mSystemServicesProxy.isForegroundUserOwner()) {
            toggleRecents();
        } else {
            Intent intent = createLocalBroadcastIntent(mContext,
                    RecentsUserEventProxyReceiver.ACTION_PROXY_TOGGLE_RECENTS_TO_USER);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
    }
}
