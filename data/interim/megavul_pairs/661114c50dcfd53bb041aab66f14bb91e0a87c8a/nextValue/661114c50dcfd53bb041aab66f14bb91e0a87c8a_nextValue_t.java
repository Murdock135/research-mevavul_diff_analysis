class nextValue {
public Object nextValue() throws JSONException {
        char c = this.nextClean();
        switch (c) {
        case '{':
            this.back();
            try {
                return new JSONObject(this);
            } catch (StackOverflowError e) {
                throw new JSONException("JSON Array or Object depth too large to process.", e);
            }
        case '[':
            this.back();
            try {
                return new JSONArray(this);
            } catch (StackOverflowError e) {
                throw new JSONException("JSON Array or Object depth too large to process.", e);
            }
        }
        return nextSimpleValue(c);
    }
}
