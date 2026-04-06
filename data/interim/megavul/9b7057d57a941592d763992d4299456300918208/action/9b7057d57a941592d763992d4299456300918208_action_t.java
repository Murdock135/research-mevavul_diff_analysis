class action {
@Override
    public boolean action(XWikiContext context) throws XWikiException
    {
        XWiki xwiki = context.getWiki();
        XWikiRequest request = context.getRequest();
        XWikiResponse response = context.getResponse();

        // Limit template overrides with xpage to allowed templates.
        if (!ALLOWED_TEMPLATES.contains(Utils.getPage(context.getRequest(), REGISTER))) {
            throw new XWikiException(XWikiException.MODULE_XWIKI, XWikiException.ERROR_XWIKI_ACCESS_DENIED,
                String.format("Forbidden template override with 'xpage' in [%s] action.", REGISTER));
        }

        String register = request.getParameter(REGISTER);
        if (register != null && register.equals("1")) {
            // CSRF prevention
            if (!csrfTokenCheck(context)) {
                return false;
            }
            // Let's verify that the user submitted the right CAPTCHA (if required).
            if (!verifyCaptcha(context, xwiki)) {
                return false;
            }

            int useemail = xwiki.getXWikiPreferenceAsInt("use_email_verification", 0, context);
            int result;
            if (useemail == 1) {
                result = xwiki.createUser(true, "edit", context);
            } else {
                result = xwiki.createUser(context);
            }
            getCurrentScriptContext().setAttribute("reg", Integer.valueOf(result), ScriptContext.ENGINE_SCOPE);

            // Redirect if a redirection parameter is passed.
            String redirect = Utils.getRedirect(request, null);
            if (redirect == null) {
                return true;
            } else {
                sendRedirect(response, redirect);
                return false;
            }
        }

        return true;
    }
}
