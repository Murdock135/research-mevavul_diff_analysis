class updateMaxLines {
private boolean updateMaxLines() {
            int maxLines = mFullLinesToDisplay;
            if (mIsShowingTruncatedText) maxLines = mTruncatedUrlLinesToDisplay;
            if (maxLines != mCurrentMaxLines) {
                setMaxLines(maxLines);
                return true;
            }
            return false;
        }
}
