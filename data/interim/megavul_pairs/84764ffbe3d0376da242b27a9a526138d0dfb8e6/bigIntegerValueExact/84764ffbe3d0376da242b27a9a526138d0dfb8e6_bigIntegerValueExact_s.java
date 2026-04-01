class bigIntegerValueExact {
@Override
    public BigInteger bigIntegerValueExact() {
        BigDecimal bd = bigDecimalValue();
        if (Math.abs(bd.scale()) <= bigIntegerScaleLimit) {
            return bd.toBigIntegerExact();
        }
        throw new UnsupportedOperationException(
                String.format(SCALE_LIMIT_EXCEPTION_MESSAGE, bd.scale(), bigIntegerScaleLimit));
    }
}
