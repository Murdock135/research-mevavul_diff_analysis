class _fromDecimal {
protected T _fromDecimal(DeserializationContext context, BigDecimal value)
    {
        FromDecimalArguments args =
            DecimalUtils.extractSecondsAndNanos(value, (s, ns) -> new FromDecimalArguments(s, ns, getZone(context)));
        return fromNanoseconds.apply(args);
    }
}
