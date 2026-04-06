class newJSONObject {
protected JSONObject newJSONObject() throws JSONException {
        checkRecursionDepth();
    	return new JSONObject(this);
    }
}
