class testExtractNanosecondDecimal03 {
@Test
    public void testExtractNanosecondDecimal03()
    {
        BigDecimal value = new BigDecimal("15.72");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 15L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value,  seconds);
        assertEquals("The nanosecond part is not correct.", 720000000, nanoseconds);
    }
}
