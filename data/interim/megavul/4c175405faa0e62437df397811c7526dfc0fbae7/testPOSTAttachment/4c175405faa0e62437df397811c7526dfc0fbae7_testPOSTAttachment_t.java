class testPOSTAttachment {
@Test
    public void testPOSTAttachment() throws Exception
    {
        final String attachmentName = String.format("%s.txt", UUID.randomUUID());
        final String content = "ATTACHMENT CONTENT";

        String attachmentsUri = buildURIForThisPage(AttachmentsResource.class, attachmentName);

        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
            TestUtils.SUPER_ADMIN_CREDENTIALS.getUserName(), TestUtils.SUPER_ADMIN_CREDENTIALS.getPassword()));
        httpClient.getParams().setAuthenticationPreemptive(true);

        Part[] parts = new Part[1];

        ByteArrayPartSource baps = new ByteArrayPartSource(attachmentName, content.getBytes());
        parts[0] = new FilePart(attachmentName, baps);

        PostMethod postMethod = new PostMethod(attachmentsUri);
        MultipartRequestEntity mpre = new MultipartRequestEntity(parts, postMethod.getParams());
        postMethod.setRequestEntity(mpre);
        postMethod.setRequestHeader("XWiki-Form-Token", getFormToken(TestUtils.SUPER_ADMIN_CREDENTIALS.getUserName(),
            TestUtils.SUPER_ADMIN_CREDENTIALS.getPassword()));
        httpClient.executeMethod(postMethod);
        Assert.assertEquals(getHttpMethodInfo(postMethod), HttpStatus.SC_CREATED, postMethod.getStatusCode());

        this.unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        Header location = postMethod.getResponseHeader("location");

        GetMethod getMethod = executeGet(location.getValue());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Assert.assertEquals(content, getMethod.getResponseBodyAsString());
    }
}
