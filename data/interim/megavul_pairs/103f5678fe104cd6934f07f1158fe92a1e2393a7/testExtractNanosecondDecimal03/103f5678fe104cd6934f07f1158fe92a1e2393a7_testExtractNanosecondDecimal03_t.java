class testExtractNanosecondDecimal03 {
@Test
    public void testExtractNanosecondDecimal03()
    {
        BigDecimal value = new BigDecimal("15.72");
        checkExtractNanos(15L, 720000000, value);
    }
}
