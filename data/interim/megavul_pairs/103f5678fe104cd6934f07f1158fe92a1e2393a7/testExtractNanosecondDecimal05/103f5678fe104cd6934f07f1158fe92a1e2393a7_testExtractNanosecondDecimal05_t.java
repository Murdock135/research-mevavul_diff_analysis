class testExtractNanosecondDecimal05 {
@Test
    public void testExtractNanosecondDecimal05()
    {
        BigDecimal value = new BigDecimal("19827342231");
        checkExtractNanos(19827342231L, 0, value);
    }
}
