class bigIntegerValue {
@Override
    public BigInteger bigIntegerValue() {
        BigDecimal bd = bigDecimalValue();
        if (bd.scale() <= bigIntegerScaleLimit) {
            return bd.toBigInteger();
        }
        throw new UnsupportedOperationException(
                String.format(
                        "Scale value %d of this BigInteger exceeded maximal allowed value of %d",
                        bd.scale(), bigIntegerScaleLimit));
    }
}
