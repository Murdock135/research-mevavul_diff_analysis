class _deserializeWrappedValue {
protected T _deserializeWrappedValue(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        // 23-Mar-2017, tatu: Let's specifically block recursive resolution to avoid
        //   either supporting nested arrays, or to cause infinite looping.
        if (p.hasToken(JsonToken.START_ARRAY)) {
            @SuppressWarnings("unchecked")
            T result = (T) handleNestedArrayForSingle(p, ctxt);
            return result;
        }
        return (T) deserialize(p, ctxt);
    }
}
