class render_3 {
@Override
    public String render(XWikiContext context) throws XWikiException
    {
        String msg = (String) context.get("message");
        if (StringUtils.isNotBlank(msg)) {
            context.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return "login";
    }
}
