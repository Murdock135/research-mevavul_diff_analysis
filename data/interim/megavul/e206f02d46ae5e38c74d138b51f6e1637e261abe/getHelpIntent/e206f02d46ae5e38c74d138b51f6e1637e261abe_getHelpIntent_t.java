class getHelpIntent {
public static Intent getHelpIntent(Context context, String helpUriString,
            String backupContext) {
        if (Global.getInt(context.getContentResolver(), Global.DEVICE_PROVISIONED, 0) == 0) {
            return null;
        }
        // Try to handle as Intent Uri, otherwise just treat as Uri.
        try {
            Intent intent = Intent.parseUri(helpUriString,
                    Intent.URI_ANDROID_APP_SCHEME | Intent.URI_INTENT_SCHEME);
            addIntentParameters(context, intent, backupContext);
            ComponentName component = intent.resolveActivity(context.getPackageManager());
            if (component != null) {
                return intent;
            } else if (intent.hasExtra(EXTRA_BACKUP_URI)) {
                // This extra contains a backup URI for when the intent isn't available.
                return getHelpIntent(context, intent.getStringExtra(EXTRA_BACKUP_URI),
                        backupContext);
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
        }
        // The help url string exists, so first add in some extra query parameters.
        final Uri fullUri = uriWithAddedParameters(context, Uri.parse(helpUriString));

        // Then, create an intent that will be fired when the user
        // selects this help menu item.
        Intent intent = new Intent(Intent.ACTION_VIEW, fullUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return intent;
    }
}
