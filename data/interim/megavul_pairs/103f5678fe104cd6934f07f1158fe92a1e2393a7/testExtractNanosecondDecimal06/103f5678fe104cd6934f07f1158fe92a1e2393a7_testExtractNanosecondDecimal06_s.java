class testExtractNanosecondDecimal06 {
@Test
    public void testExtractNanosecondDecimal06()
    {
        BigDecimal value = new BigDecimal("19827342231.999999999");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 19827342231L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value,  seconds);
        assertEquals("The nanosecond part is not correct.", 999999999, nanoseconds);
    }
}
