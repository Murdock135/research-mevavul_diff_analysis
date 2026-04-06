class testDeserializationAsFloatEdgeCase08_1 {
@Test
    public void testDeserializationAsFloatEdgeCase08() throws Exception
    {
        String input = "1e10000000";
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                                 .readValue(input);
        assertEquals(0, value.getSeconds());
    }
}
