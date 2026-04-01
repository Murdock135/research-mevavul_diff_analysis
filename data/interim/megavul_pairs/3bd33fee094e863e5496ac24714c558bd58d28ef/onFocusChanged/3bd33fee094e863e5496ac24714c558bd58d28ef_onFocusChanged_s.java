class onFocusChanged {
@Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        mFocused = focused;
        if (!focused) mAutocompleteSpan.clearSpan();
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused && mFirstFocusTimeMs == 0) {
            mFirstFocusTimeMs = SystemClock.elapsedRealtime();
            if (mOmniboxLivenessListener != null) mOmniboxLivenessListener.onOmniboxFocused();
        }

        if (focused) StartupMetrics.getInstance().recordFocusedOmnibox();
    }
}
