class contains {
private boolean contains(final String value, final String text, final Boolean ignoreCase) {
        if (value != null && text != null) {
            String val = value.trim();
            String txt = text.trim();

            if (ignoreCase != null && ignoreCase) {
                val = val.toLowerCase();
                txt = text.toLowerCase();
            }

            return val.contains(txt);
        }

        return false;
    }
}
