class bigIntegerValueExact {
@Override
    public BigInteger bigIntegerValueExact() {
        BigDecimal bd = bigDecimalValue();
        if (Math.abs(bd.scale()) <= bigIntegerScaleLimit) {
            return bd.toBigIntegerExact();
        }
        throw new UnsupportedOperationException(
                JsonMessages.NUMBER_SCALE_LIMIT_EXCEPTION(bd.scale(), bigIntegerScaleLimit));
    }
}
