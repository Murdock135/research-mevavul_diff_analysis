class prepareHelpMenuItem {
public static boolean prepareHelpMenuItem(final Activity activity, MenuItem helpMenuItem,
            String helpUriString, String backupContext) {
        if (TextUtils.isEmpty(helpUriString)) {
            // The help url string is empty or null, so set the help menu item to be invisible.
            helpMenuItem.setVisible(false);

            // return that the help menu item is not visible (i.e. false)
            return false;
        } else {
            final Intent intent = getHelpIntent(activity, helpUriString, backupContext);

            // Set the intent to the help menu item, show the help menu item in the overflow
            // menu, and make it visible.
            if (intent != null) {
                helpMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            activity.startActivityForResult(intent, 0);
                        } catch (ActivityNotFoundException exc) {
                            Log.e(TAG, "No activity found for intent: " + intent);
                        }
                        return true;
                    }
                });
                helpMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                helpMenuItem.setVisible(true);
            } else {
                helpMenuItem.setVisible(false);
                return false;
            }

            // return that the help menu item is visible (i.e., true)
            return true;
        }
    }
}
