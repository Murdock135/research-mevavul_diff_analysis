class equals {
private boolean equals(final String value, final String text, final Boolean ignoreCase) {
        if (value != null && text != null) {
            String val = value.trim();
            String txt = text.trim();

            if (ignoreCase != null && ignoreCase) {
                val = val.toLowerCase();
                txt = text.toLowerCase();
            }

            if (val.equals(txt)) {
                return true;
            }
        }

        return false;
    }
}
