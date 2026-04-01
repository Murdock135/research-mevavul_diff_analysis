class testDeserializationAsFloatEdgeCase08 {
@Test
    public void testDeserializationAsFloatEdgeCase08() throws Exception
    {
        String input = "1e10000000";
        Instant value = MAPPER.readValue(input, Instant.class);
        assertEquals(0, value.getEpochSecond());
    }
}
