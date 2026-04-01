class _parseLongPrimitive {
protected final long _parseLongPrimitive(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        String text;
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            text = p.getText();
            break;
        case JsonTokenId.ID_NUMBER_FLOAT:
            final CoercionAction act = _checkFloatToIntCoercion(p, ctxt, Long.TYPE);
            if (act == CoercionAction.AsNull) {
                return 0L;
            }
            if (act == CoercionAction.AsEmpty) {
                return 0L;
            }
            return p.getValueAsLong();
        case JsonTokenId.ID_NUMBER_INT:
            return p.getLongValue();
        case JsonTokenId.ID_NULL:
            _verifyNullForPrimitive(ctxt);
            return 0L;
        // 29-Jun-2020, tatu: New! "Scalar from Object" (mostly for XML)
        case JsonTokenId.ID_START_OBJECT:
            text = ctxt.extractScalarFromObject(p, this, Long.TYPE);
            break;
        case JsonTokenId.ID_START_ARRAY:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                p.nextToken();
                final long parsed = _parseLongPrimitive(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
            // fall through
        default:
            return ((Number) ctxt.handleUnexpectedToken(Long.TYPE, p)).longValue();
        }

        final CoercionAction act = _checkFromStringCoercion(ctxt, text,
                LogicalType.Integer, Long.TYPE);
        if (act == CoercionAction.AsNull) {
            // 03-May-2021, tatu: Might not be allowed (should we do "empty" check?)
            _verifyNullForPrimitive(ctxt);
            return 0L; 
        }
        if (act == CoercionAction.AsEmpty) {
            return 0L;
        }
        text = text.trim();
        if (_hasTextualNull(text)) {
            _verifyNullForPrimitiveCoercion(ctxt, text);
            return 0L;
        }
        return _parseLongPrimitive(ctxt, text);
    }
}
