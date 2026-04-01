class appendText {
public void appendText(String text, final boolean doNotAppend) {
        if (text == null) {
            return;
        }
        final Editable editable = this.binding.textinput.getText();
        String previous =  editable == null ? "" : editable.toString();
        if (doNotAppend && !TextUtils.isEmpty(previous)) {
            Toast.makeText(getActivity(),R.string.already_drafting_message, Toast.LENGTH_LONG).show();
            return;
        }
        if (UIHelper.isLastLineQuote(previous)) {
            text = '\n' + text;
        } else if (previous.length() != 0 && !Character.isWhitespace(previous.charAt(previous.length() - 1))) {
            text = " " + text;
        }
        this.binding.textinput.append(text);
    }
}
