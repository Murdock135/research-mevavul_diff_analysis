class newJSONArray {
protected JSONArray newJSONArray() throws JSONException {
        checkRecursionDepth();
    	return new JSONArray(this);
    }
}
