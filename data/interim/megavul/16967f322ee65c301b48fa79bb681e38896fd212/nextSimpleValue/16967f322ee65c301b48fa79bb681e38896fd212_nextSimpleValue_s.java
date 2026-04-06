class nextSimpleValue {
Object nextSimpleValue(char c) {
        String string;

        switch (c) {
        case '"':
        case '\'':
            return this.nextString(c);
        case '{':
            throw syntaxError("Nested object not expected here.");
        case '[':
            throw syntaxError("Nested array not expected here.");
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuilder sb = new StringBuilder();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = this.next();
        }
        if (!this.eof) {
            this.back();
        }

        string = sb.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }
        return JSONObject.stringToValue(string);
    }
}
