class bigIntegerValue {
@Override
    public BigInteger bigIntegerValue() {
        BigDecimal bd = bigDecimalValue();
        if (Math.abs(bd.scale()) <= bigIntegerScaleLimit) {
            return bd.toBigInteger();
        }
        throw new UnsupportedOperationException(
                String.format(SCALE_LIMIT_EXCEPTION_MESSAGE, bd.scale(), bigIntegerScaleLimit));
    }
}
