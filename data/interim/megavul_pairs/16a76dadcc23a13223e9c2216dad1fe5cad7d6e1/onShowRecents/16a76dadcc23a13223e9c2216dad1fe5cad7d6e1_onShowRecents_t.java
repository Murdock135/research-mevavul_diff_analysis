class onShowRecents {
@ProxyFromPrimaryToCurrentUser
    public void onShowRecents(boolean triggeredFromAltTab) {
        // Ensure the device has been provisioned before allowing the user to interact with
        // recents
        if (!isDeviceProvisioned()) {
            return;
        }

        if (mSystemServicesProxy.isForegroundUserOwner()) {
            showRecents(triggeredFromAltTab);
        } else {
            Intent intent = createLocalBroadcastIntent(mContext,
                    RecentsUserEventProxyReceiver.ACTION_PROXY_SHOW_RECENTS_TO_USER);
            intent.putExtra(EXTRA_TRIGGERED_FROM_ALT_TAB, triggeredFromAltTab);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
    }
}
