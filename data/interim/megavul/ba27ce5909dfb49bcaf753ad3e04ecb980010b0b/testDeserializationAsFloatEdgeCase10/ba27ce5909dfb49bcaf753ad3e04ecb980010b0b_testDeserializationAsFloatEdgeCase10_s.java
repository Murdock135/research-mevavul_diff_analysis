class testDeserializationAsFloatEdgeCase10 {
@Test
    public void testDeserializationAsFloatEdgeCase10() throws Exception
    {
        String input = "1e-10000000";
        Instant value = MAPPER.readValue(input, Instant.class);
        assertEquals(0, value.getEpochSecond());
    }
}
