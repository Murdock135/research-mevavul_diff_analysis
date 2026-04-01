class _parseShortPrimitive {
protected final short _parseShortPrimitive(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        String text;
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            text = p.getText();
            break;
        case JsonTokenId.ID_NUMBER_FLOAT:
            CoercionAction act = _checkFloatToIntCoercion(p, ctxt, Short.TYPE);
            if (act == CoercionAction.AsNull) {
                return (short) 0;
            }
            if (act == CoercionAction.AsEmpty) {
                return (short) 0;
            }
            return p.getShortValue();
        case JsonTokenId.ID_NUMBER_INT:
            return p.getShortValue();
        case JsonTokenId.ID_NULL:
            _verifyNullForPrimitive(ctxt);
            return (short) 0;
        // 29-Jun-2020, tatu: New! "Scalar from Object" (mostly for XML)
        case JsonTokenId.ID_START_OBJECT:
            text = ctxt.extractScalarFromObject(p, this, Short.TYPE);
            break;
        case JsonTokenId.ID_START_ARRAY:
            // 12-Jun-2020, tatu: For some reason calling `_deserializeFromArray()` won't work so:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                p.nextToken();
                final short parsed = _parseShortPrimitive(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
            // fall through to fail
        default:
            return ((Short) ctxt.handleUnexpectedToken(ctxt.constructType(Short.TYPE), p)).shortValue();
        }

        CoercionAction act = _checkFromStringCoercion(ctxt, text,
                LogicalType.Integer, Short.TYPE);
        if (act == CoercionAction.AsNull) {
            // 03-May-2021, tatu: Might not be allowed (should we do "empty" check?)
            _verifyNullForPrimitive(ctxt);
            return (short) 0;
        }
        if (act == CoercionAction.AsEmpty) {
            return (short) 0;
        }
        text = text.trim();
        if (_hasTextualNull(text)) {
            _verifyNullForPrimitiveCoercion(ctxt, text);
            return (short) 0;
        }
        int value;
        try {
            value = NumberInput.parseInt(text);
        } catch (IllegalArgumentException iae) {
            return (Short) ctxt.handleWeirdStringValue(Short.TYPE, text,
                    "not a valid `short` value");
        }
        if (_shortOverflow(value)) {
            return (Short) ctxt.handleWeirdStringValue(Short.TYPE, text,
                    "overflow, value cannot be represented as 16-bit value");
        }
        return (short) value;
    }
}
