class testExtractNanosecondDecimal05 {
@Test
    public void testExtractNanosecondDecimal05()
    {
        BigDecimal value = new BigDecimal("19827342231");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 19827342231L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value,  seconds);
        assertEquals("The nanosecond part is not correct.", 0, nanoseconds);
    }
}
