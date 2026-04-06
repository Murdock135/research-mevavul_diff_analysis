class executePost {
protected PostMethod executePost(String uri, String string, String mediaType, String userName, String password)
        throws Exception
    {
        return executePost(uri, string, mediaType, userName, password, getFormToken(userName, password));
    }
}
