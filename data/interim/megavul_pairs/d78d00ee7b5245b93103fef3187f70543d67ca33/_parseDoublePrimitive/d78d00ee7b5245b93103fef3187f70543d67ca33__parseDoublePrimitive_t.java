class _parseDoublePrimitive {
protected final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        String text;
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            text = p.getText();
            break;
        case JsonTokenId.ID_NUMBER_INT:
            final CoercionAction act = _checkIntToFloatCoercion(p, ctxt, Double.TYPE);
            if (act == CoercionAction.AsNull) {
                return 0.0d;
            }
            if (act == CoercionAction.AsEmpty) {
                return 0.0d;
            }
            // fall through to coerce
        case JsonTokenId.ID_NUMBER_FLOAT:
            return p.getDoubleValue();
        case JsonTokenId.ID_NULL:
            _verifyNullForPrimitive(ctxt);
            return 0.0;
        // 29-Jun-2020, tatu: New! "Scalar from Object" (mostly for XML)
        case JsonTokenId.ID_START_OBJECT:
            text = ctxt.extractScalarFromObject(p, this, Double.TYPE);
            break;
        case JsonTokenId.ID_START_ARRAY:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                if (p.nextToken() == JsonToken.START_ARRAY) {
                    return (double) handleNestedArrayForSingle(p, ctxt);
                }
                final double parsed = _parseDoublePrimitive(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
            // fall through
        default:
            return ((Number) ctxt.handleUnexpectedToken(Double.TYPE, p)).doubleValue();
        }

        // 18-Nov-2020, tatu: Special case, Not-a-Numbers as String need to be
        //     considered "native" representation as JSON does not allow as numbers,
        //     and hence not bound by coercion rules
        {
            Double nan = this._checkDoubleSpecialValue(text);
            if (nan != null) {
                return nan.doubleValue();
            }
        }

        final CoercionAction act = _checkFromStringCoercion(ctxt, text,
                LogicalType.Integer, Double.TYPE);
        if (act == CoercionAction.AsNull) {
            // 03-May-2021, tatu: Might not be allowed (should we do "empty" check?)
            _verifyNullForPrimitive(ctxt);
            return  0.0;
        }
        if (act == CoercionAction.AsEmpty) {
            return  0.0;
        }
        text = text.trim();
        if (_hasTextualNull(text)) {
            _verifyNullForPrimitiveCoercion(ctxt, text);
            return  0.0;
        }
        return _parseDoublePrimitive(p, ctxt, text);
    }
}
