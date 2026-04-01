class _parseIntPrimitive {
protected final int _parseIntPrimitive(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        String text;
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            text = p.getText();
            break;
        case JsonTokenId.ID_NUMBER_FLOAT:
            final CoercionAction act = _checkFloatToIntCoercion(p, ctxt, Integer.TYPE);
            if (act == CoercionAction.AsNull) {
                return 0;
            }
            if (act == CoercionAction.AsEmpty) {
                return 0;
            }
            return p.getValueAsInt();
        case JsonTokenId.ID_NUMBER_INT:
            return p.getIntValue();
        case JsonTokenId.ID_NULL:
            _verifyNullForPrimitive(ctxt);
            return 0;
        // 29-Jun-2020, tatu: New! "Scalar from Object" (mostly for XML)
        case JsonTokenId.ID_START_OBJECT:
            text = ctxt.extractScalarFromObject(p, this, Integer.TYPE);
            break;
        case JsonTokenId.ID_START_ARRAY:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                if (p.nextToken() == JsonToken.START_ARRAY) {
                    return (int) handleNestedArrayForSingle(p, ctxt);
                }
                final int parsed = _parseIntPrimitive(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
            // fall through to fail
        default:
            return ((Number) ctxt.handleUnexpectedToken(Integer.TYPE, p)).intValue();
        }

        final CoercionAction act = _checkFromStringCoercion(ctxt, text,
                LogicalType.Integer, Integer.TYPE);
        if (act == CoercionAction.AsNull) {
            // 03-May-2021, tatu: Might not be allowed (should we do "empty" check?)
            _verifyNullForPrimitive(ctxt);
            return 0;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0;
        }
        text = text.trim();
        if (_hasTextualNull(text)) {
            _verifyNullForPrimitiveCoercion(ctxt, text);
            return 0;
        }
        return _parseIntPrimitive(ctxt, text);
    }
}
