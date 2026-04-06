class testWithMaliciousRequest {
@Test
    public void testWithMaliciousRequest() throws IOException, WebdavException {
        when(request.getReader()).thenReturn(getResource("/malicious-request.xml"));
        assertTrue(requestPars.processXml());
    }
}
