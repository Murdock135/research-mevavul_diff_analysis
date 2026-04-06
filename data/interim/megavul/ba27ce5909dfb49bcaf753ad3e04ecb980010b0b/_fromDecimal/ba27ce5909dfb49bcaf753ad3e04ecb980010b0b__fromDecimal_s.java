class _fromDecimal {
protected T _fromDecimal(DeserializationContext context, BigDecimal value)
    {
        long seconds = value.longValue();
        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        return fromNanoseconds.apply(new FromDecimalArguments(
                seconds, nanoseconds, getZone(context)));
    }
}
