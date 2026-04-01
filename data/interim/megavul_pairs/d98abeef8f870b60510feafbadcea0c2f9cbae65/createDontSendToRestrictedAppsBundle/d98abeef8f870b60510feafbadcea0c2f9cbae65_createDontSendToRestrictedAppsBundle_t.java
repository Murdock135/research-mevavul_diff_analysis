class createDontSendToRestrictedAppsBundle {
public static Bundle createDontSendToRestrictedAppsBundle(@Nullable Bundle bundle) {
        final BroadcastOptions options = BroadcastOptions.makeBasic();
        options.setDontSendToRestrictedApps(true);
        options.setPendingIntentBackgroundActivityLaunchAllowed(false);
        if (bundle == null) {
            return options.toBundle();
        }
        bundle.putAll(options.toBundle());
        return bundle;
    }
}
