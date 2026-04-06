class executePost {
protected PostMethod executePost(String uri, String string, String mediaType, String userName, String password)
        throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        PostMethod postMethod = new PostMethod(uri);
        postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());

        RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
        postMethod.setRequestEntity(entity);

        httpClient.executeMethod(postMethod);

        return postMethod;
    }
}
