class getOrMaskValue {
private String getOrMaskValue(String value) {
        return maskSensitiveFields ? MASK_FOR_SENSITIVE_DATA : value;
    }
}
