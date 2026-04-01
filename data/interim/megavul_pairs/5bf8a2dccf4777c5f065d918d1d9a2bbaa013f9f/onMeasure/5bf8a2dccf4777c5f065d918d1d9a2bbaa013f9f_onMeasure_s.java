class onMeasure {
@Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMaxLines(Integer.MAX_VALUE);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            assert mProfile != null : "setProfile() must be called before layout.";

            // Lay out the URL in a StaticLayout that is the same size as our final
            // container.
            Layout layout = getLayout();
            int originEndIndex =
                    OmniboxUrlEmphasizer.getOriginEndIndex(getText().toString(), mProfile);

            // Find the range of lines containing the origin.
            int originEndLineIndex = 0;
            while (originEndLineIndex < layout.getLineCount()
                    && layout.getLineEnd(originEndLineIndex) < originEndIndex) {
                originEndLineIndex++;
            }

            // Display an extra line so we don't accidentally hide the origin with
            // ellipses
            int lastLineIndexToDisplay = originEndLineIndex + 1;

            // Since lastLineToDisplay is an index, add 1 to get the maximum number
            // of lines. This will always be at least 2 lines (when the origin is
            // fully contained on line 0).
            mTruncatedUrlLinesToDisplay = lastLineIndexToDisplay + 1;

            if (updateMaxLines()) super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
}
