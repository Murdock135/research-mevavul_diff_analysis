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

        // When unfocused, force left-to-right rendering at the paragraph level (which is desired
        // for URLs). Right-to-left runs are still rendered RTL, but will not flip the whole URL
        // around. This is consistent with OmniboxViewViews on desktop. When focused, render text
        // normally (to allow users to make non-URL searches and to avoid showing Android's split
        // insertion point when an RTL user enters RTL text).
        if (focused) {
            ApiCompatibilityUtils.setTextDirection(this, TEXT_DIRECTION_INHERIT);
        } else {
            ApiCompatibilityUtils.setTextDirection(this, TEXT_DIRECTION_LTR);
        }
        // Always align to the same as the paragraph direction (LTR = left, RTL = right).
        ApiCompatibilityUtils.setTextAlignment(this, TEXT_ALIGNMENT_TEXT_START);
    }
}
