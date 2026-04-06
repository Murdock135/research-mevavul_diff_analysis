class render_4 {
@Override
    public String render(XWikiContext context) throws XWikiException
    {
        // if the request does not come from an explicit login link (such as the login button)
        // then we consider it's coming from a redirect because a guest user tries to access a protected resource
        if (!"1".equals(context.getRequest().getParameter("loginLink"))) {
            context.getResponse().setStatus(401);
        }
        return "login";
    }
}
