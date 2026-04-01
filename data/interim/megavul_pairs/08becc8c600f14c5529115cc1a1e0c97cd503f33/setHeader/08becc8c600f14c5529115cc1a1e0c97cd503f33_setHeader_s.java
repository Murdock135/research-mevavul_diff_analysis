class setHeader {
private void setHeader(View decor, FillResponse response) {
        final RemoteViews presentation = response.getDialogHeader();
        if (presentation == null) {
            return;
        }

        final ViewGroup container = decor.findViewById(R.id.autofill_dialog_header);
        final RemoteViews.InteractionHandler interceptionHandler = (view, pendingIntent, r) -> {
            if (pendingIntent != null) {
                mCallback.startIntentSender(pendingIntent.getIntentSender());
            }
            return true;
        };

        final View content = presentation.applyWithTheme(
                mContext, (ViewGroup) decor, interceptionHandler, mThemeId);
        container.addView(content);
        container.setVisibility(View.VISIBLE);
    }
}
