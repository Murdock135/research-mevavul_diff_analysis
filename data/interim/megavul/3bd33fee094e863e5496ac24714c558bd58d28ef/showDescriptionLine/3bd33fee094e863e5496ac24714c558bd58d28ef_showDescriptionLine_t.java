class showDescriptionLine {
private void showDescriptionLine(Spannable str, boolean isUrl) {
        TextView textLine = mContentsView.mTextLine2;
        if (textLine.getVisibility() != VISIBLE) {
            textLine.setVisibility(VISIBLE);
        }
        textLine.setText(str, BufferType.SPANNABLE);

        // Force left-to-right rendering for URLs. See UrlBar constructor for details.
        if (isUrl) {
            textLine.setTextColor(URL_COLOR);
            ApiCompatibilityUtils.setTextDirection(textLine, TEXT_DIRECTION_LTR);
        } else {
            textLine.setTextColor(getStandardFontColor());
            ApiCompatibilityUtils.setTextDirection(textLine, TEXT_DIRECTION_INHERIT);
        }
    }
}
