class testPostRequestParsWithMaliciousRequest {
@Test
    public void testPostRequestParsWithMaliciousRequest() throws WebdavException {
        assertTrue(requestPars.processXml());
    }
}
