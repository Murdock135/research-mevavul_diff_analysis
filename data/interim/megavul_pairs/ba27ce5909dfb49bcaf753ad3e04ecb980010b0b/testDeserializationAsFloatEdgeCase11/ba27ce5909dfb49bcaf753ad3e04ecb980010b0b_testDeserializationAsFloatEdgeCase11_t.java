class testDeserializationAsFloatEdgeCase11 {
@Test(timeout = 100)
    public void testDeserializationAsFloatEdgeCase11() throws Exception
    {
        String input = "-1e-10000000";
        Instant value = MAPPER.readValue(input, Instant.class);
        assertEquals(0, value.getEpochSecond());
    }
}
