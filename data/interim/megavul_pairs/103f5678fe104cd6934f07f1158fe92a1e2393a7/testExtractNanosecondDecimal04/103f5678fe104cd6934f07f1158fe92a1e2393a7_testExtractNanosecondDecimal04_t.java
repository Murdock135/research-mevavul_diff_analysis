class testExtractNanosecondDecimal04 {
@Test
    public void testExtractNanosecondDecimal04()
    {
        BigDecimal value = new BigDecimal("19827342231.192837465");
        checkExtractNanos(19827342231L, 192837465, value);
    }
}
