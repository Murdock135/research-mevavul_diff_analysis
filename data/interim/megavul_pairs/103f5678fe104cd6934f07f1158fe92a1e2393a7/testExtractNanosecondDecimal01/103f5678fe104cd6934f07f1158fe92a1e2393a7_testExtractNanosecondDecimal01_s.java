class testExtractNanosecondDecimal01 {
@Test
    public void testExtractNanosecondDecimal01()
    {
        BigDecimal value = new BigDecimal("0");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 0L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value,  seconds);
        assertEquals("The nanosecond part is not correct.", 0, nanoseconds);
    }
}
