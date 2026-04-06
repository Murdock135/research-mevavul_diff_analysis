class render {
@Override
    public String render(XWikiContext context) throws XWikiException
    {
        try {
            return render(context.getRequest().getPathInfo(), context);
        } catch (IOException e) {
            context.getResponse().setStatus(404);
            return DOCDOESNOTEXIST;
        }
    }
}
