class testMethodBaseWithMaliciousRequest {
@Test
    public void testMethodBaseWithMaliciousRequest() throws WebdavException {
        assertNotNull(methodBase.parseContent(request, response));
    }
}
