class setup {
@Before
  public void setup() throws WebdavException, IOException {
    when(request.getContentType()).thenReturn("application/xml");
    requestPars = new PostRequestPars(request, webdavNsIntf, UUID.randomUUID().toString());

    when(methodBase.getNsIntf()).thenReturn(webdavNsIntf);

    when(request.getContentLength()).thenReturn(1);
    when(request.getReader()).thenReturn(getResource("/malicious-request.xml"));
  }
}
