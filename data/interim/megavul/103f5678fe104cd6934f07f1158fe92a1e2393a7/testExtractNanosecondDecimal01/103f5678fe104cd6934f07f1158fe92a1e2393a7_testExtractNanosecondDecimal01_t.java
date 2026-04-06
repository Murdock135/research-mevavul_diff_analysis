class testExtractNanosecondDecimal01 {
@Test
    public void testExtractNanosecondDecimal01()
    {
        BigDecimal value = new BigDecimal("0");
        checkExtractNanos(0L, 0, value);
    }
}
