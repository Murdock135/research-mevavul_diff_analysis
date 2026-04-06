class showDescriptionLine {
private void showDescriptionLine(Spannable str, int textColor) {
        if (mContentsView.mTextLine2.getVisibility() != VISIBLE) {
            mContentsView.mTextLine2.setVisibility(VISIBLE);
        }
        mContentsView.mTextLine2.setTextColor(textColor);
        mContentsView.mTextLine2.setText(str, BufferType.SPANNABLE);
    }
}
