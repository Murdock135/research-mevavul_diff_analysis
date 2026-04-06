class put {
public JSONArray put(Collection value) throws JSONException {
        put(new JSONArray(value));
        return this;
    }
}
