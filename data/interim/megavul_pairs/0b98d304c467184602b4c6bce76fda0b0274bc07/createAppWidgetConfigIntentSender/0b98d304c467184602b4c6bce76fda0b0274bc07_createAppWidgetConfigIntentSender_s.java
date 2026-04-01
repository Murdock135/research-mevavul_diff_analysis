class createAppWidgetConfigIntentSender {
@Override
    public IntentSender createAppWidgetConfigIntentSender(String callingPackage, int appWidgetId,
            int intentFlags) {
        final int userId = UserHandle.getCallingUserId();

        if (DEBUG) {
            Slog.i(TAG, "createAppWidgetConfigIntentSender() " + userId);
        }

        // Make sure the package runs under the caller uid.
        mSecurityPolicy.enforceCallFromPackage(callingPackage);

        synchronized (mLock) {
            ensureGroupStateLoadedLocked(userId);

            // NOTE: The lookup is enforcing security across users by making
            // sure the caller can only access widgets it hosts or provides.
            Widget widget = lookupWidgetLocked(appWidgetId,
                    Binder.getCallingUid(), callingPackage);

            if (widget == null) {
                throw new IllegalArgumentException("Bad widget id " + appWidgetId);
            }

            Provider provider = widget.provider;
            if (provider == null) {
                throw new IllegalArgumentException("Widget not bound " + appWidgetId);
            }

            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setComponent(provider.info.configure);
            intent.setFlags(intentFlags);

            // All right, create the sender.
            final long identity = Binder.clearCallingIdentity();
            try {
                return PendingIntent.getActivityAsUser(
                        mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT
                                | PendingIntent.FLAG_CANCEL_CURRENT, null,
                                new UserHandle(provider.getUserId()))
                        .getIntentSender();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }
}
