class testExtractNanosecondDecimal04 {
@Test
    public void testExtractNanosecondDecimal04()
    {
        BigDecimal value = new BigDecimal("19827342231.192837465");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 19827342231L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value,  seconds);
        assertEquals("The nanosecond part is not correct.", 192837465, nanoseconds);
    }
}
