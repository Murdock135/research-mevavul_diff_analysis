class onMeasure {
@Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMaxLines(Integer.MAX_VALUE);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            assert mProfile != null : "setProfile() must be called before layout.";
            String urlText = getText().toString();

            // Lay out the URL in a StaticLayout that is the same size as our final
            // container.
            int originEndIndex = OmniboxUrlEmphasizer.getOriginEndIndex(urlText, mProfile);

            // Find the range of lines containing the origin.
            int originEndLine = getLineForIndex(originEndIndex);

            // Display an extra line so we don't accidentally hide the origin with
            // ellipses
            mTruncatedUrlLinesToDisplay = originEndLine + 1;

            // Find the line where the fragment starts. Since # is a reserved character, it is safe
            // to just search for the first # to appear in the url.
            int fragmentStartIndex = urlText.indexOf('#');
            if (fragmentStartIndex == -1) fragmentStartIndex = urlText.length();

            int fragmentStartLine = getLineForIndex(fragmentStartIndex);
            mFullLinesToDisplay = fragmentStartLine + 1;

            // If there is no origin (according to OmniboxUrlEmphasizer), make sure the fragment is
            // still hidden correctly.
            if (mFullLinesToDisplay < mTruncatedUrlLinesToDisplay) {
                mTruncatedUrlLinesToDisplay = mFullLinesToDisplay;
            }

            if (updateMaxLines()) super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
}
