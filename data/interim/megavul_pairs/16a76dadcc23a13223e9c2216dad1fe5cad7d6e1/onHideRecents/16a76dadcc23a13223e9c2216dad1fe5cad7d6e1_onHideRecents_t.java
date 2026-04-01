class onHideRecents {
@ProxyFromPrimaryToCurrentUser
    public void onHideRecents(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
        // Ensure the device has been provisioned before allowing the user to interact with
        // recents
        if (!isDeviceProvisioned()) {
            return;
        }

        if (mSystemServicesProxy.isForegroundUserOwner()) {
            hideRecents(triggeredFromAltTab, triggeredFromHomeKey);
        } else {
            Intent intent = createLocalBroadcastIntent(mContext,
                    RecentsUserEventProxyReceiver.ACTION_PROXY_HIDE_RECENTS_TO_USER);
            intent.putExtra(EXTRA_TRIGGERED_FROM_ALT_TAB, triggeredFromAltTab);
            intent.putExtra(EXTRA_TRIGGERED_FROM_HOME_KEY, triggeredFromHomeKey);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
    }
}
