class testExtractNanosecondDecimal02 {
@Test
    public void testExtractNanosecondDecimal02()
    {
        BigDecimal value = new BigDecimal("15.000000072");
        checkExtractNanos(15L, 72, value);
    }
}
