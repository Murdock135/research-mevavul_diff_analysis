class testExtractNanosecondDecimal06 {
@Test
    public void testExtractNanosecondDecimal06()
    {
        BigDecimal value = new BigDecimal("19827342231.999999999");
        checkExtractNanos(19827342231L, 999999999, value);
    }
}
