class getOrMaskValue {
private String getOrMaskValue(String value) {
        if (value == null) {
            return null;
        }
        return maskSensitiveFields ? MASK_FOR_SENSITIVE_DATA : value;
    }
}
