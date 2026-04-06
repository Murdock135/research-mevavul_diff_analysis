class bigIntegerValue {
@Override
    public BigInteger bigIntegerValue() {
        BigDecimal bd = bigDecimalValue();
        if (Math.abs(bd.scale()) <= bigIntegerScaleLimit) {
            return bd.toBigInteger();
        }
        throw new UnsupportedOperationException(
                JsonMessages.NUMBER_SCALE_LIMIT_EXCEPTION(bd.scale(), bigIntegerScaleLimit));
    }
}
