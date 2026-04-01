class testExtractNanosecondDecimal02 {
@Test
    public void testExtractNanosecondDecimal02()
    {
        BigDecimal value = new BigDecimal("15.000000072");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 15L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value,  seconds);
        assertEquals("The nanosecond part is not correct.", 72, nanoseconds);
    }
}
