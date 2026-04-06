class _deserializeWrappedValue {
protected T _deserializeWrappedValue(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        // 23-Mar-2017, tatu: Let's specifically block recursive resolution to avoid
        //   either supporting nested arrays, or to cause infinite looping.
        if (p.hasToken(JsonToken.START_ARRAY)) {
            String msg = String.format(
"Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s",
                    ClassUtil.nameOf(_valueClass), JsonToken.START_ARRAY,
                    "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
            @SuppressWarnings("unchecked")
            T result = (T) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, msg);
            return result;
        }
        return (T) deserialize(p, ctxt);
    }
}
