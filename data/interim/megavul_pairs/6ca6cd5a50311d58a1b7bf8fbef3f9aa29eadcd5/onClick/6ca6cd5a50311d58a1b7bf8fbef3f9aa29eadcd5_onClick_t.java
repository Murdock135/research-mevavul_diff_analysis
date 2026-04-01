class onClick {
@Override
    public void onClick(View v) {
        if (v == mSettingsButton) {
            MetricsLogger.action(mContext,
                    MetricsProto.MetricsEvent.ACTION_QS_EXPANDED_SETTINGS_LAUNCH);
            if (mSettingsButton.isTunerClick()) {
                mHost.startRunnableDismissingKeyguard(() -> post(() -> {
                    if (TunerService.isTunerEnabled(mContext)) {
                        TunerService.showResetRequest(mContext, () -> {
                            // Relaunch settings so that the tuner disappears.
                            startSettingsActivity();
                        });
                    } else {
                        Toast.makeText(getContext(), R.string.tuner_toast,
                                Toast.LENGTH_LONG).show();
                        TunerService.setTunerEnabled(mContext, true);
                    }
                    startSettingsActivity();

                }));
            } else {
                startSettingsActivity();
            }
        } else if (v == mAlarmStatus && mNextAlarm != null) {
            PendingIntent showIntent = mNextAlarm.getShowIntent();
            if (showIntent != null && showIntent.isActivity()) {
                mActivityStarter.startActivity(showIntent.getIntent(), true /* dismissShade */);
            }
        }
    }
}
