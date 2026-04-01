class setUrlText {
private boolean setUrlText(OmniboxResultItem result) {
        OmniboxSuggestion suggestion = result.getSuggestion();
        Spannable str = SpannableString.valueOf(suggestion.getDisplayText());
        boolean hasMatch = applyHighlightToMatchRegions(
                str, suggestion.getDisplayTextClassifications());
        showDescriptionLine(str, true);
        return hasMatch;
    }
}
