class testDeserializationAsFloatEdgeCase09 {
@Test(timeout = 100)
    public void testDeserializationAsFloatEdgeCase09() throws Exception
    {
        String input = "-1e10000000";
        Instant value = MAPPER.readValue(input, Instant.class);
        assertEquals(0, value.getEpochSecond());
    }
}
