class injectSendIntentSender {
void injectSendIntentSender(IntentSender intentSender, Intent extras) {
        if (intentSender == null) {
            return;
        }
        try {
            intentSender.sendIntent(mContext, /* code= */ 0, extras,
                    /* onFinished=*/ null, /* handler= */ null);
        } catch (SendIntentException e) {
            Slog.w(TAG, "sendIntent failed().", e);
        }
    }
}
