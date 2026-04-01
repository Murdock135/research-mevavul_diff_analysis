class injectSendIntentSender {
void injectSendIntentSender(IntentSender intentSender, Intent extras) {
        if (intentSender == null) {
            return;
        }
        try {
            ActivityOptions options = ActivityOptions.makeBasic()
                    .setPendingIntentBackgroundActivityStartMode(
                            MODE_BACKGROUND_ACTIVITY_START_DENIED);
            intentSender.sendIntent(mContext, /* code= */ 0, extras,
                    /* onFinished=*/ null, /* handler= */ null, null, options.toBundle());
        } catch (SendIntentException e) {
            Slog.w(TAG, "sendIntent failed().", e);
        }
    }
}
