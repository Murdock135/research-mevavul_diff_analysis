class patternMatches {
private boolean patternMatches(EasyField easyField, Object val) {
        if (!(easyField instanceof EasyText)) return true;

        Pattern patt = ((EasyText) easyField).getPattern();
        return patt == null || patt.matcher((CharSequence) val).matches();
    }
}
