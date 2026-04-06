class _parseDateFromArray {
protected java.util.Date _parseDateFromArray(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        final CoercionAction act = _findCoercionFromEmptyArray(ctxt);
        final boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

        if (unwrap || (act != CoercionAction.Fail)) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                switch (act) {
                case AsEmpty:
                    return (java.util.Date) getEmptyValue(ctxt);
                case AsNull:
                case TryConvert:
                    return (java.util.Date) getNullValue(ctxt);
                default:
                }
            } else if (unwrap) {
                final Date parsed = _parseDate(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
        }
        return (java.util.Date) ctxt.handleUnexpectedToken(_valueClass, JsonToken.START_ARRAY, p, null);
    }
}
