class _deserializeFromArray {
@Override
    protected Object _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        // note: cannot call `_delegateDeserializer()` since order reversed here:
        JsonDeserializer<Object> delegateDeser = _arrayDelegateDeserializer;
        // fallback to non-array delegate
        if ((delegateDeser != null) || ((delegateDeser = _delegateDeserializer) != null)) {
            Object bean = _valueInstantiator.createUsingArrayDelegate(ctxt,
                    delegateDeser.deserialize(p, ctxt));
            if (_injectables != null) {
                injectValues(ctxt, bean);
            }
            return bean;
        }
        final CoercionAction act = _findCoercionFromEmptyArray(ctxt);
        final boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

        if (unwrap || (act != CoercionAction.Fail)) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                switch (act) {
                case AsEmpty:
                    return getEmptyValue(ctxt);
                case AsNull:
                case TryConvert:
                    return getNullValue(ctxt);
                default:
                }
                return ctxt.handleUnexpectedToken(getValueType(ctxt), JsonToken.START_ARRAY, p, null);
            }
            if (unwrap) {
                final Object value = deserialize(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(p, ctxt);
                }
                return value;
            }
        }
        return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }
}
